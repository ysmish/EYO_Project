import { generateToken, authorizeToken } from '../models/tokens.js';

const createToken = (req, res) => {
    const { username, password } = req.body;
    
    if (!username || !password) {
        return res.status(400).json({ error: 'Username and password are required.' });
    }
    
    const result = generateToken(username, password);
    
    if (result.error) {
        return res.status(401).json({ error: result.error });
    }
    
    return res.status(201).json({ token: result.token });
}

const validateToken = (req, res) => {
    const { token } = req.body;

    if (!token) {
        return res.status(401).json({ error: 'Token is required.' });
    }

    const result = authorizeToken(token);

    if (result.error) {
        return res.status(401).json({ error: result.error });
    }

    return res.status(200).json({ username: result.username });
}

export { createToken, validateToken };