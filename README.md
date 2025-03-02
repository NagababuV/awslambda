First, create a user in the IAM section of AWS with full access to S3. Using the access key of this user, you will be able to upload objects to an S3 bucket using Java SDK.![Screenshot 2025-03-02 at 11 25 18 AM](https://github.com/user-attachments/assets/b121294a-3193-4297-8962-9bc2d273e65a)

Steps to Build the Application 
- Set Up AWS Resources
Create two S3 buckets: one for initial uploads and one for backups.
Create an IAM role with permissions for S3 and Lambda.
Create an AWS Lambda function to handle the backup process.

- Backend - Spring Boot (Java)
Implement an API to handle file uploads to the primary S3 bucket.
Configure AWS SDK to interact with S3.
Generate a pre-signed URL (optional) to allow secure file upload.
Trigger an AWS Lambda function after a successful upload.

- AWS Lambda
Listen for S3 events from the primary bucket.
Copy the uploaded file to the backup bucket.

 - Create a Role
Go to AWS IAM.
Click Roles on the left panel.
Click Create Role.
Under Trusted entity type, select AWS Service.
Choose Lambda as the use case.
Click Next.

Attach Policies to Allow S3 Access
In Permissions, click Create Policy (or attach an existing one).
Go to the JSON tab and paste the following policy:
json
Copy
Edit
![Screenshot 2025-03-02 at 11 56 50 AM](https://github.com/user-attachments/assets/f271553d-05c4-4135-b017-bb3883142f1c)
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "s3:GetObject",
                "s3:ListBucket"
            ],
            "Resource": [
                "arn:aws:s3:::naga-source-s3-bucket",
                "arn:aws:s3:::naga-source-s3-bucket/*"
            ]
        },
        {
            "Effect": "Allow",
            "Action": [
                "s3:PutObject"
            ],
            "Resource": [
                "arn:aws:s3:::naga-backup-s3-bucket",
                "arn:aws:s3:::naga-backup-s3-bucket/*"
            ]
        }
    ]
}
![Screenshot 2025-03-02 at 11 58 25 AM](https://github.com/user-attachments/assets/aa119b74-7b0f-4be9-a695-eb54367fbaf1)
![Screenshot 2025-03-02 at 12 00 10 PM](https://github.com/user-attachments/assets/45750fff-90b9-4353-8161-44fc09f370d0)




