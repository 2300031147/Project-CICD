# Setup Guide

## Quick Start (Recommended)

### Using Docker Compose

1. **Prerequisites**
   - Install Docker Desktop (includes Docker Compose)
   - Ensure ports 3000, 8080, and 3306 are available

2. **Clone and Start**
   ```bash
   git clone https://github.com/2300031147/Project-CICD.git
   cd Project-CICD
   docker-compose up -d
   ```

3. **Wait for Services**
   Wait about 2 minutes for all services to start up.
   - MySQL takes ~30 seconds
   - Backend takes ~60 seconds (waits for MySQL)
   - Frontend takes ~20 seconds

4. **Populate Sample Data** (Optional)
   ```bash
   ./populate-db.sh
   ```

5. **Access Application**
   - Frontend: http://localhost:3000
   - Backend API: http://localhost:8080
   - Backend Health: http://localhost:8080/actuator/health (if enabled)

6. **Create Your First Account**
   - Navigate to http://localhost:3000
   - Click "Sign up"
   - Fill in your details
   - Login and start exploring!

## Local Development Setup

### Backend Development

1. **Prerequisites**
   - Java 17+
   - Maven 3.6+
   - MySQL 8.0

2. **Setup MySQL**
   ```bash
   # Start MySQL (if not using Docker)
   mysql -u root -p
   ```
   
   ```sql
   CREATE DATABASE musicdb;
   ```

3. **Configure Application**
   Edit `backend/src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/musicdb
   spring.datasource.username=root
   spring.datasource.password=1108
   ```

4. **Run Backend**
   ```bash
   cd backend
   mvn spring-boot:run
   ```
   
   Backend will start at http://localhost:8080

5. **Verify Backend**
   ```bash
   curl http://localhost:8080/api/songs/top
   ```

### Frontend Development

1. **Prerequisites**
   - Node.js 18+
   - npm 8+

2. **Install Dependencies**
   ```bash
   cd frontend
   npm install
   ```

3. **Configure API URL**
   Create `.env` file in frontend directory:
   ```
   REACT_APP_API_URL=http://localhost:8080/api
   ```

4. **Run Frontend**
   ```bash
   npm start
   ```
   
   Frontend will start at http://localhost:3000

## Testing

### Backend Tests
```bash
cd backend
mvn test
```

### Frontend Tests
```bash
cd frontend
npm test
```

### Integration Tests
```bash
# Start all services
docker-compose up -d

# Run integration tests (if available)
# ./run-integration-tests.sh
```

## Troubleshooting

### Port Already in Use
If ports are already in use, modify `docker-compose.yml`:
```yaml
services:
  frontend:
    ports:
      - "3001:80"  # Changed from 3000
```

### Backend Won't Start
1. Check MySQL is running:
   ```bash
   docker-compose ps
   ```

2. Check backend logs:
   ```bash
   docker-compose logs backend
   ```

3. Verify database connection:
   ```bash
   docker exec -it music-mysql mysql -uroot -p1108 musicdb
   ```

### Frontend Can't Connect to Backend
1. Check CORS settings in backend
2. Verify backend is running: `curl http://localhost:8080/api/songs/top`
3. Check browser console for errors

### Database Not Populated
1. Ensure MySQL is fully started:
   ```bash
   docker-compose logs mysql
   ```

2. Run populate script:
   ```bash
   ./populate-db.sh
   ```

3. Manually check data:
   ```bash
   docker exec -it music-mysql mysql -uroot -p1108 musicdb
   SELECT COUNT(*) FROM songs;
   ```

## Production Deployment

### Environment Variables

**Backend:**
- `SPRING_DATASOURCE_URL`: Database connection string
- `SPRING_DATASOURCE_USERNAME`: Database username
- `SPRING_DATASOURCE_PASSWORD`: Database password
- `JWT_SECRET`: JWT signing secret (change in production!)
- `JWT_EXPIRATION`: Token expiration time in milliseconds

**Frontend:**
- `REACT_APP_API_URL`: Backend API URL

### Docker Compose for Production

1. Update `docker-compose.yml` with production settings
2. Use environment-specific configuration files
3. Set up proper volumes for data persistence
4. Configure reverse proxy (nginx/Apache)
5. Enable HTTPS/SSL certificates

### Security Checklist

- [ ] Change default database password
- [ ] Generate new JWT secret
- [ ] Enable HTTPS
- [ ] Configure firewall rules
- [ ] Set up rate limiting
- [ ] Enable logging and monitoring
- [ ] Regular security updates

## Additional Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [React Documentation](https://react.dev)
- [Docker Documentation](https://docs.docker.com)
- [MySQL Documentation](https://dev.mysql.com/doc)

## Support

For issues and questions:
- Check existing issues on GitHub
- Create a new issue with detailed description
- Include logs and error messages
