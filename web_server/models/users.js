import User from '../service/User.js';
import Label from '../service/Label.js';

const getUser = async (username) => {
    if (!username) {
        return { error: 'Username is required' }
    }
    
    try {
        const user = await User.findOne({ id: username });
        if (!user) {
            return { error: 'User not found' }
        }
        return {
            id: user.id,
            firstName: user.firstName,
            lastName: user.lastName,
            birthday: user.birthday.toISOString().split('T')[0], // Format as YYYY-MM-DD
            email: user.email,
            photo: user.photo
        }
    } catch (error) {
        return { error: 'Database error: ' + error.message }
    }
}

const addUser = async (firstName, lastName, birthday, username, password, photo = null) => {
    if (!firstName || !lastName || !birthday || !username || !password) {
        return { error: 'Invalid user data. Please provide firstName, lastName, birthday, username, and password.'}
    }
    
    try {
        // Check if user already exists
        const existingUser = await User.findOne({ id: username });
        if (existingUser) {
            return { error: 'Username already exists' }
        }

        // Simple validation for username and password
        if (username.length < 3 || password.length < 6) {
            return { error: 'Username must be at least 3 characters and password at least 6 characters long'}
        }
        if (!/^[a-zA-Z0-9]+$/.test(username)) {
            return { error: 'Username can only contain alphanumeric characters' }
        }

        if (!/^[a-zA-Z0-9]+$/.test(firstName) || !/^[a-zA-Z0-9]+$/.test(lastName)) {
            return { error: 'First name and last name can only contain alphanumeric characters'}
        }

        if (!/^\d{4}-\d{2}-\d{2}$/.test(birthday) || isNaN(new Date(birthday).getTime()) || new Date(birthday) > new Date()) {
            return { error: 'Invalid birthday format. Use YYYY-MM-DD and ensure it is a valid date in the past.' }
        }

        // Create a new user
        const newUser = new User({
            id: username,
            firstName,
            lastName,
            birthday: new Date(birthday),
            email: `${username}@eyo.com`,
            password, // TODO: ensure to hash the password
            photo: photo || 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMTAwIiBoZWlnaHQ9IjEwMCIgdmlld0JveD0iMCAwIDEwMCAxMDAiIGZpbGw9Im5vbmUiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+CjxyZWN0IHdpZHRoPSIxMDAiIGhlaWdodD0iMTAwIiBmaWxsPSIjRjNGNEY2Ii8+CjxjaXJjbGUgY3g9IjUwIiBjeT0iMzUiIHI9IjE1IiBmaWxsPSIjOUNBM0FGIi8+CjxwYXRoIGQ9Ik0yMCA4MEM3NS4wNzY5IDc5LjIzMDggMjQgNzUuMDc2OSAyNCA3MEMyNCA2NC45MjMxIDc1LjA3NjkgNjAgODAgNjAiIGZpbGw9IiM5Q0EzQUYiLz4KPC9zdmc+'
        });

        const savedUser = await newUser.save();

        // Create default labels for the new user
        const defaultLabels = [
            { id: 1, name: 'Inbox', username },
            { id: 2, name: 'Sent', username },
            { id: 3, name: 'Starred', username },
            { id: 4, name: 'Drafts', username },
            { id: 5, name: 'Spam', username }
        ];

        await Label.insertMany(defaultLabels);

        return {
            id: savedUser.id,
            firstName: savedUser.firstName,
            lastName: savedUser.lastName,
            birthday: savedUser.birthday,
            email: savedUser.email,
            photo: savedUser.photo
        };
    } catch (error) {
        return { error: 'Database error: ' + error.message }
    }
}

const changePhoto = async (username, photo) => {
    if (!username) {
        return { error: 'Username and photo are required' }
    }
    
    try {
        const user = await User.findOne({ id: username });
        if (!user) {
            return { error: 'User not found' }
        }
        
        user.photo = photo || 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMTAwIiBoZWlnaHQ9IjEwMCIgdmlld0JveD0iMCAwIDEwMCAxMDAiIGZpbGw9Im5vbmUiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+CjxyZWN0IHdpZHRoPSIxMDAiIGhlaWdodD0iMTAwIiBmaWxsPSIjRjNGNEY2Ii8+CjxjaXJjbGUgY3g9IjUwIiBjeT0iMzUiIHI9IjE1IiBmaWxsPSIjOUNBM0FGIi8+CjxwYXRoIGQ9Ik0yMCA4MEM3NS4wNzY5IDc5LjIzMDggMjQgNzUuMDc2OSAyNCA3MEMyNCA2NC45MjMxIDc1LjA3NjkgNjAgODAgNjAiIGZpbGw9IiM5Q0EzQUYiLz4KPC9zdmc+'; // Default placeholder avatar
        
        await user.save();
        return { message: 'Photo updated successfully' };
    } catch (error) {
        return { error: 'Database error: ' + error.message }
    }
}

// Exporting functions for use in controllers
export { addUser, getUser, changePhoto };
