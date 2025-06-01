import { deleteUrl } from '../models/blacklist.js';

const addURL = (req, res) => {
        return res.status(200).json({
            message: 'Add URL endpoint is not implemented yet'
        });
}

const deleteURL = (req, res) => {
    const url = req.body.url;
    try {
        const result = deleteUrl(url);
        if (result) {
            return res.status(204).json({
                message: 'URL deleted successfully'
            });
        } else {
            return res.status(404).json({
                message: 'URL not found'
            });
        }
    } catch (err) {
        return res.status(500).json({
            message: 'Internal server error'
        });
    }
}

export { addURL, deleteURL };