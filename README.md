# File Management

## User guide
To start go to `application.conf` and set the following properties:
- `aws.s3.bucket.name` and `aws.s3.bucket.region` to access to your AWS
- `db.properties.*` to access to your PostgreSQL database
<br>Then, launch the application using `sbt run` and go to http://localhost:9000
