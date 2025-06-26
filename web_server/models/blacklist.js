import net from 'net';

const createTcpRequest = (method, url) => {
    return new Promise((resolve, reject) => {
        const client = new net.Socket();
        let response = '';

        client.on('data', (data) => {
            const chunk = data.toString();
            response += chunk;
            if (response.endsWith('\n')) {
                client.end();
            }
        });

        client.on('end', () => {
            client.destroy();
            resolve(response.trim());
        });

        client.on('error', (err) => {
            client.destroy();
            reject(new Error(`TCP connection error: ${err.message}`));
        });

        try {
            client.connect(12345, 'localhost', () => { 
                client.write(`${method} ${url}\n`);
            });
        } catch (err) {
            client.destroy();
            reject(new Error(`Failed to establish connection: ${err.message}`));
        }
    });
};

const checkUrl = async (url) => {
    try {
        const response = await createTcpRequest('GET', url);
        const isBlacklisted = response.includes('true true');
        return isBlacklisted;
    } catch (err) {
        console.error('Error in checkUrl:', err.message);
        return false;  // Return false instead of throwing on connection error
    }
};

const addUrl = async (url) => {
    try {
        const response = await createTcpRequest('POST', url);
        const success = response.includes('201 Created');
        return success;
    } catch (err) {
        console.error('Error in addUrl:', err.message);
        return false;  // Return false instead of throwing on connection error
    }
};

const deleteUrl = async (url) => {
    try {
        const response = await createTcpRequest('DELETE', url);
        const success = response.includes('204 No Content');
        return success;
    } catch (err) {
        console.error('Error in deleteUrl:', err.message);
        return false;  // Return false instead of throwing on connection error
    }
};

export { checkUrl, deleteUrl, addUrl }; 
