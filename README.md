# Instagram Reel Uploader

A Spring Boot application for uploading and scheduling Instagram reels programmatically.

## Project Structure

```
insta-reel-uploader/
│
├── .gradle/
├── .idea/
├── build/
│
├── gradle/
│   └── wrapper/
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── insta/
│   │   │           └── uploader/
│   │   │               ├── InstaUploaderApplication.java
│   │   │               ├── controller/
│   │   │               │   └── ReelController.java
│   │   │               ├── service/
│   │   │               │   └── InstagramService.java
│   │   │               ├── scheduler/
│   │   │               │   └── ReelScheduler.java
│   │   │               └── model/
│   │   │                   └── ReelRequest.java
│   │   │
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── static/
│   │       └── templates/
│   │
│   └── test/
│       └── java/
│           └── com/
│               └── insta/
│                   └── uploader/
│                       └── InstaUploaderApplicationTests.java
│
├── .gitignore
├── build.gradle
├── settings.gradle
├── gradlew
├── gradlew.bat
└── README.md
```

## Features

- **REST API for Reel Upload**: Upload reels immediately
- **Scheduled Upload**: Schedule reels for upload at specific times
- **Background Scheduling**: Automatic processing of scheduled reels
- **Health Check Endpoint**: Monitor service status
- **Comprehensive Logging**: Debug and info level logging for troubleshooting

## Prerequisites

- Java 23 or higher
- Gradle 8.0 or higher
- Instagram Business Account with API access
- Instagram Graph API token

## Installation

1. Clone the repository:
```bash
git clone <repository-url>
cd insta-reel-uploader
```

2. Set up environment variables:
```bash
export INSTAGRAM_API_TOKEN=<your-api-token>
export INSTAGRAM_BUSINESS_ACCOUNT_ID=<your-business-account-id>
```

3. Build the project:
```bash
./gradlew build
```

## Running the Application

### Development Mode
```bash
./gradlew bootRun
```

### Production Mode
```bash
java -jar build/libs/InstaUploaderApplication-*.jar
```

The application will start on `http://localhost:8080`

## API Endpoints

### Upload Reel Immediately
**POST** `/api/reels/upload`

Request Body:
```json
{
  "reel_id": "reel_001",
  "caption": "My awesome reel! #instagram #automation",
  "file_path": "/path/to/video.mp4",
  "hashtags": "#instagram #automation #reels",
  "scheduled_time": null
}
```

Response:
```json
{
  "status": "success",
  "message": "Reel uploaded successfully",
  "reelId": "reel_001"
}
```

### Schedule Reel for Later Upload
**POST** `/api/reels/schedule`

Request Body:
```json
{
  "reel_id": "reel_002",
  "caption": "Coming soon!",
  "file_path": "/path/to/video.mp4",
  "hashtags": "#instagram #automation",
  "scheduled_time": "2026-03-28T15:30:00"
}
```

Response:
```json
{
  "status": "success",
  "message": "Reel scheduled successfully",
  "reelId": "reel_002",
  "scheduledTime": "2026-03-28T15:30:00"
}
```

### Health Check
**GET** `/api/reels/health`

Response:
```json
{
  "status": "up",
  "service": "Instagram Reel Uploader"
}
```

## Configuration

Edit `src/main/resources/application.yml` to customize:

- **Server Port**: Default is 8080
- **Context Path**: Default is `/api`
- **Logging Level**: Set to DEBUG for verbose logging
- **Instagram API Token**: Set via environment variable or properties file
- **Scheduler Thread Pool Size**: Default is 2

## Components

### InstaUploaderApplication
Main Spring Boot application class that initializes the application and enables scheduling.

### ReelController
REST controller handling HTTP requests for reel upload and scheduling operations.

### InstagramService
Business logic service for:
- Uploading reels to Instagram
- Scheduling reels for later upload
- Validating reel requests
- Handling Instagram API interactions

### ReelScheduler
Background scheduler component that:
- Processes scheduled reels every hour
- Performs health checks every 30 minutes
- Manages automatic upload of scheduled reels

### ReelRequest
Data model representing a reel upload request with:
- Reel ID
- Caption
- File path
- Scheduled time
- Hashtags

## Development

### Building the Project
```bash
./gradlew build
```

### Running Tests
```bash
./gradlew test
```

### Running with DevTools
The application includes Spring Boot DevTools for automatic restart on file changes.

## Dependencies

- Spring Boot 4.0.5
- Spring Web MVC
- Spring Boot DevTools
- JUnit Platform
- Jackson (for JSON processing)

## Future Enhancements

- [ ] Database integration for storing reel metadata
- [ ] User authentication and authorization
- [ ] Multiple account management
- [ ] Webhook support for Instagram events
- [ ] Advanced scheduling options (recurring uploads)
- [ ] Analytics and reporting dashboard
- [ ] Image/Thumbnail support
- [ ] Hashtag suggestions and optimization

## Troubleshooting

### Application Won't Start
- Ensure Java 23 is installed: `java -version`
- Check logs for configuration errors
- Verify environment variables are set correctly

### API Returns 401 Unauthorized
- Verify Instagram API token is valid
- Check token has not expired
- Ensure business account ID is correct

### Scheduled Uploads Not Running
- Enable scheduling: Check `application.yml` has `scheduler.enabled: true`
- Review logs for scheduler errors
- Verify scheduled times are in the future

## License

This project is licensed under the MIT License.

## Support

For issues and questions, please create an issue in the repository.

## Contributing

Contributions are welcome! Please follow these steps:
1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Submit a pull request

---

**Last Updated**: March 28, 2026
**Version**: 0.0.1-SNAPSHOT

