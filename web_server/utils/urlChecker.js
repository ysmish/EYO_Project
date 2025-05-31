import net from 'net';

const checkUrl = async (url) => {
    return new Promise((resolve, reject) => {
        const client = new net.Socket();
        
        client.connect(12345, 'url_server', () => {
            client.write(url + '\n');
        });

        client.on('data', (data) => {
            const response = data.toString().trim();
            client.destroy();
            resolve(response === '1'); // '1' means URL is allowed, '0' means blacklisted
        });

        client.on('error', (err) => {
            client.destroy();
            reject(err);
        });
    });
};

export { checkUrl }; 