import jwt from 'jsonwebtoken';
import User from '../service/User.js';
import dotenv from 'dotenv';

// Load environment variables
dotenv.config();

const JWT_SECRET = process.env.JWT_SECRET;

const generateToken = async (username, password) => {
    if (!username || !password) {
        return { error: 'Username and password are required.' };
    }

    try {
        const user = await User.findOne({ id: username, password: password });
        
        if (!user) {
            return { error: 'Invalid username or password.' };
        }
        
        // Generate JWT token with 2-hour expiration
        const token = jwt.sign(
            { username: user.id },
            JWT_SECRET,
            { expiresIn: '2h' }
        );
        
        return { token };
    } catch (error) {
        return { error: 'Database error: ' + error.message };
    }
}

const authorizeToken = (token) => {
    if (!token) {
        return { error: 'Token is required.' };
    }

    try {
        // Verify the JWT token
        const decoded = jwt.verify(token, JWT_SECRET);
        return { username: decoded.username };
    } catch (error) {
        if (error.name === 'TokenExpiredError') {
            return { error: 'Token has expired.' };
        } else if (error.name === 'JsonWebTokenError') {
            return { error: 'Invalid token.' };
        } else {
            return { error: 'Token verification failed.' };
        }
    }
}

export { generateToken, authorizeToken };