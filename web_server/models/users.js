let users = []

const getUser = (username) => {
    if (!username) {
        return { error: 'Username is required' }
    }
    const user = users.find(user => user.id === username);
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
}

const addUser = (firstName, lastName, birthday, username, password, photo=null) => {
    if (!firstName || !lastName || !birthday || !username || !password) {
        return { error: 'Invalid user data. Please provide firstName, lastName, birthday, username, and password.'}
    }
    
    if (users.some(user => user.id === username)) {
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

    // Create a new user object
    const newUser = {
        id: username, // Using username as a unique identifier
        firstName,
        lastName,
        birthday: new Date(birthday), // Store as a Date object
        email: `${username}@example.com`, // Simple email generation for demonstration
        password, // TODO: ensure to hash the password
        photo: photo || 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMTAwIiBoZWlnaHQ9IjEwMCIgdmlld0JveD0iMCAwIDEwMCAxMDAiIGZpbGw9Im5vbmUiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+CjxyZWN0IHdpZHRoPSIxMDAiIGhlaWdodD0iMTAwIiBmaWxsPSIjRjNGNEY2Ii8+CjxjaXJjbGUgY3g9IjUwIiBjeT0iMzUiIHI9IjE1IiBmaWxsPSIjOUNBM0FGIi8+CjxwYXRoIGQ9Ik0yMCA4MEM3NS4wNzY5IDc5LjIzMDggMjQgNzUuMDc2OSAyNCA3MEMyNCA2NC45MjMxIDc1LjA3NjkgNjAgODAgNjAiIGZpbGw9IiM5Q0EzQUYiLz4KPC9zdmc+', // Default placeholder avatar
    };
    users.push(newUser);
    return newUser;
}

const changePhoto = (username, photo) => {
    if (!username || !photo) {
        return { error: 'Username and photo are required' }
    }
    const user = users.find(user => user.id === username);
    if (!user) {
        return { error: 'User not found' }
    }
    user.photo = photo;
    return { message: 'Photo updated successfully' };
}

// Exporting functions for use in controllers
export { addUser, getUser, changePhoto, users };
