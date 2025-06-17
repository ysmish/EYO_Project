import { generateToken } from '../models/tokens.js';

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
export { createToken };