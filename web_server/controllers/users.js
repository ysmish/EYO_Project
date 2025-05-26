import {} from '../models/users.js';

const createNewUser = (req, res) => {
        return res.status(200).json({
            message: 'Add user endpoint is not implemented yet'
        });
}

const getUserById = (req, res) => {
    return res.status(200).json({
        message: 'Get user by ID endpoint is not implemented yet'
    });
}

export { createNewUser, getUserById };