#!/bin/bash

# Script to populate the database with sample data
# Usage: ./populate-db.sh

echo "Populating database with sample data..."

# Wait for MySQL to be ready
until docker exec music-mysql mysqladmin ping -h "localhost" -uroot -p1108 --silent; do
    echo "Waiting for MySQL..."
    sleep 2
done

echo "MySQL is ready. Inserting sample data..."

# Execute SQL file
docker exec -i music-mysql mysql -uroot -p1108 musicdb < backend/src/main/resources/data.sql

echo "Sample data inserted successfully!"
echo ""
echo "Sample accounts created:"
echo "  - You can now register a new account via the UI"
echo ""
echo "Access the application at:"
echo "  - Frontend: http://localhost:3000"
echo "  - Backend: http://localhost:8080"
