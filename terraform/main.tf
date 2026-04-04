provider "aws" {
  region = "sa-east-1"
}

resource "aws_ecr_repository" "app" {
  name                 = "oslo-ia"
  image_tag_mutability = "MUTABLE"

  image_scanning_configuration {
    scan_on_push = true
  }
}

output "ecr_repository_url" {
  value = aws_ecr_repository.app.repository_url
}
# VPC padrão
data "aws_vpc" "default" {
  default = true
}

# Subnets padrão
data "aws_subnets" "default" {
  filter {
    name   = "vpc-id"
    values = [data.aws_vpc.default.id]
  }
}

# Security Group para o RDS
resource "aws_security_group" "rds" {
  name   = "oslo-ia-rds-sg"
  vpc_id = data.aws_vpc.default.id

  ingress {
    from_port   = 5432
    to_port     = 5432
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

# Subnet Group para o RDS
resource "aws_db_subnet_group" "rds" {
  name       = "oslo-ia-subnet-group"
  subnet_ids = data.aws_subnets.default.ids
}

# RDS PostgreSQL
resource "aws_db_instance" "postgres" {
  identifier           = "oslo-ia-db"
  engine               = "postgres"
  engine_version       = "15"
  instance_class       = "db.t3.micro"
  allocated_storage    = 20
  db_name              = "oslo_ia"
  username             = "postgres"
  password             = "postgres123"
  publicly_accessible  = true
  skip_final_snapshot  = true
  db_subnet_group_name = aws_db_subnet_group.rds.name
  vpc_security_group_ids = [aws_security_group.rds.id]
}

output "rds_endpoint" {
  value = aws_db_instance.postgres.endpoint
}
# IAM Role para o EKS
resource "aws_iam_role" "eks" {
  name = "oslo-ia-eks-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Action = "sts:AssumeRole"
      Effect = "Allow"
      Principal = {
        Service = "eks.amazonaws.com"
      }
    }]
  })
}

resource "aws_iam_role_policy_attachment" "eks_policy" {
  policy_arn = "arn:aws:iam::aws:policy/AmazonEKSClusterPolicy"
  role       = aws_iam_role.eks.name
}

# EKS Cluster
resource "aws_eks_cluster" "cluster" {
  name     = "oslo-ia-cluster"
  role_arn = aws_iam_role.eks.arn

  vpc_config {
    subnet_ids = data.aws_subnets.default.ids
  }

  depends_on = [aws_iam_role_policy_attachment.eks_policy]
}

# IAM Role para o Node Group
resource "aws_iam_role" "nodes" {
  name = "oslo-ia-nodes-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Action = "sts:AssumeRole"
      Effect = "Allow"
      Principal = {
        Service = "ec2.amazonaws.com"
      }
    }]
  })
}

resource "aws_iam_role_policy_attachment" "nodes_worker" {
  policy_arn = "arn:aws:iam::aws:policy/AmazonEKSWorkerNodePolicy"
  role       = aws_iam_role.nodes.name
}

resource "aws_iam_role_policy_attachment" "nodes_cni" {
  policy_arn = "arn:aws:iam::aws:policy/AmazonEKS_CNI_Policy"
  role       = aws_iam_role.nodes.name
}

resource "aws_iam_role_policy_attachment" "nodes_ecr" {
  policy_arn = "arn:aws:iam::aws:policy/AmazonEC2ContainerRegistryReadOnly"
  role       = aws_iam_role.nodes.name
}

# Node Group
resource "aws_eks_node_group" "workers" {
  cluster_name    = aws_eks_cluster.cluster.name
  node_group_name = "workers"
  node_role_arn   = aws_iam_role.nodes.arn
  subnet_ids      = data.aws_subnets.default.ids

  instance_types = ["t3.small"]

  scaling_config {
    desired_size = 1
    max_size     = 1
    min_size     = 1
  }

  depends_on = [
    aws_iam_role_policy_attachment.nodes_worker,
    aws_iam_role_policy_attachment.nodes_cni,
    aws_iam_role_policy_attachment.nodes_ecr
  ]
}

output "eks_cluster_name" {
  value = aws_eks_cluster.cluster.name
}aws eks update-kubeconfig --name oslo-ia-cluster --region sa-east-1