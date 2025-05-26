import {} from '../models/blacklist.js';

const addURL = (req, res) => {
        return res.status(200).json({
            message: 'Add URL endpoint is not implemented yet'
        });
}

const deleteURL = (req, res) => {
    return res.status(200).json({
        message: 'Delete URL endpoint is not implemented yet'
    });
}

export { addURL, deleteURL };