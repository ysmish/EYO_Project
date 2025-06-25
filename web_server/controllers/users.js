import { addUser, getUser, changePhoto } from '../models/users.js';
import { authorizeToken } from '../models/tokens.js';

const createNewUser = (req, res) => {
    const userData = req.body;
    if (!userData || !userData.firstName || !userData.lastName || !userData.birthday || !userData.username || !userData.password) {
        return res.status(400).json({
            message: 'Invalid user data. Please provide firstName, lastName, birthday, username, and password.'
        });
    }
    try {
        const newUser = addUser(
            userData.firstName,
            userData.lastName,
            userData.birthday,
            userData.username,
            userData.password,
            userData.photo
        );
        if (newUser.error) {
            let statusCode = 400;
            if (newUser.error === 'Username already exists') {
                statusCode = 409; // Conflict for existing username
            }
            return res.status(statusCode).json({
                message: 'Error creating user',
                error: newUser.error
            });
        }
        return res.status(201).location(`api/users/${newUser.id}`).json({message: 'User created successfully'});
    } catch (error) {
        return res.status(500).json({
            message: 'Error creating user',
            error: error.message
        });
    }
}

const getUserById = (req, res) => {
    const username = req.params.id;
    if (!username) {
        return res.status(400).json({
            message: 'Username is required'
        });
    }
    try {
        const user = getUser(username);
        if (user.error) {
            return res.status(404).json({
                message: 'User not found',
                error: user.error
            });
        }
        return res.status(200).json(user);
    } catch (error) {
        return res.status(500).json({
            message: 'Error retrieving user',
            error: error.message
        });
    }
}

const changeUserPhoto = (req, res) => {
    const token = req.headers.authorization;
    if (!token) {
        return res.status(400).json({ error: 'Authorization token is required.' });
    }

    // Verify JWT token
    const authResult = authorizeToken(token);
    if (authResult.error) {
        return res.status(401).json({ error: authResult.error });
    }
    const username = authResult.username;
    let photo = "";
    // Use multer: req.file contains the uploaded file
    if (!req.file) {
        photo = "";
    } else {
        // Convert buffer to base64 Data URL (assuming image)
        const mimeType = req.file.mimetype;
        const base64 = req.file.buffer.toString('base64');
        photo = `data:${mimeType};base64,${base64}`;
    }



    try {
        const result = changePhoto(username, photo);
        if (result.error) {
            return res.status(404).json({ error: result.error });
        }
        return res.status(200).json({ message: 'Photo updated successfully' });
    } catch (error) {
        console.error('Error updating photo:', error);
        return res.status(500).json({ error: 'Internal server error.' });
    }
}

export { createNewUser, getUserById, changeUserPhoto };