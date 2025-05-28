import { users } from './users.js';

const generateToken = (username, password) => {
    if (!username || !password) {
        return { error: 'Username and password are required.' };
    }

    const user = users.find(user => user.id === username && user.password === password);
    
    if (!user) {
        return { error: 'Invalid username or password.' };
    }
    
    return { username: user.id };
}


export { generateToken };