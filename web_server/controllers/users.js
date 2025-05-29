import { addUser, getUser } from '../models/users.js';

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

export { createNewUser, getUserById };