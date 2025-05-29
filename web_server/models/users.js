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
        photo: photo || 'https://i.sstatic.net/frlIf.png', // Default photo if none provided
    };
    users.push(newUser);
    return newUser;
}


export { addUser, getUser, users };
