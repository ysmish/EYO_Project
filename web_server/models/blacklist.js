import net from 'net';

const checkUrl = (url) => {
    try {
        const client = new net.Socket();
        client.connect(12345, 'url_server', () => {
            client.write('GET ' + url + '\n');
            let response = '';
            const buffer = Buffer.alloc(1024);
            let bytesRead;
            while ((bytesRead = client.read(buffer)) > 0) {
                response += buffer.toString('utf8', 0, bytesRead);
            }
            client.destroy();
            return (response.trim() === '200 OK\n\ntrue true\n');
        });
    } catch (err) {
        client.destroy();
        throw err;
    }
};

const deleteUrl = (url) => {
    try {
        const client = new net.Socket();
        client.connect(12345, 'url_server', () => {
            client.write('DELETE ' + url + '\n');
            let response = '';
            const buffer = Buffer.alloc(1024);
            let bytesRead;
            while ((bytesRead = client.read(buffer)) > 0) {
                response += buffer.toString('utf8', 0, bytesRead);
            }
            client.destroy();
            return (response.trim() === '204 No Content\n');
        });
    } catch (err) {
        client.destroy();
        throw err;
    }
};

export { checkUrl, deleteUrl }; 