-- Create the database if it doesn't exist
CREATE DATABASE IF NOT EXISTS chess;

-- Use the chess database
USE chess;

-- Create the users table
CREATE TABLE IF NOT EXISTS users (
                                     user_id INT AUTO_INCREMENT PRIMARY KEY,
                                     username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

-- Create the games table
CREATE TABLE IF NOT EXISTS games (
                                     game_id INT AUTO_INCREMENT PRIMARY KEY,
                                     game_name VARCHAR(100) NOT NULL,
    white_player_id INT,
    black_player_id INT,
    game_state TEXT NOT NULL,
    current_turn ENUM('WHITE', 'BLACK') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (white_player_id) REFERENCES users(user_id),
    FOREIGN KEY (black_player_id) REFERENCES users(user_id)
    );

-- Create the auth_tokens table
CREATE TABLE IF NOT EXISTS auth_tokens (
                                           token_id INT AUTO_INCREMENT PRIMARY KEY,
                                           user_id INT NOT NULL,
                                           auth_token VARCHAR(255) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
    );
