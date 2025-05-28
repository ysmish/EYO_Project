import { generateToken } from '../models/tokens.js';

const createToken = (req, res) => {
    const { username, password } = req.body;
    
    if (!username || !password) {
        return res.status(400).json({ error: 'Username and password are required.' });
    }
    
    const token = generateToken(username, password);
    
    if (token.error) {
        return res.status(401).json({ error: token.error });
    }
    
    return res.status(201).json({ token: token.username });
}
export { createToken };