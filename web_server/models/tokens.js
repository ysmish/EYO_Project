import jwt from 'jsonwebtoken';
import { users } from './users.js';

// Secret key for JWT signing (change it later to a strong secret key in production)
const JWT_SECRET = 'your-secret-key-here';

const generateToken = (username, password) => {
    if (!username || !password) {
        return { error: 'Username and password are required.' };
    }

    const user = users.find(user => user.id === username && user.password === password);
    
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