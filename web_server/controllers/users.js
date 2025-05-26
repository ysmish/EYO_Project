import { addUser } from '../models/users.js';

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
            return res.status(400).json({
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
    return res.status(200).json({
        message: 'Get user by ID endpoint is not implemented yet'
    });
}

export { createNewUser, getUserById };