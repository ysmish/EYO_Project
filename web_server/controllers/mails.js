import { getLatestMails } from '../models/mails.js';

const getAllMails = (req, res) => {
    const username = req.headers.authorization;
    if (!username) {
        return res.status(400).json({ error: 'Username is required.' });
    }
    try {
        const latestMails = getLatestMails();
        return res.status(200).json({data: latestMails});
    } catch (error) {
        return res.status(500).json({error: 'Failed to fetch mails' });
    }
}

const  getMailById = (req, res) => {
        return res.status(200).json({
            message: 'Add Mail endpoint is not implemented yet'
        });
}

const createMail = (req, res) => {
        return res.status(200).json({
            message: 'Add Mail endpoint is not implemented yet'
        });
}

const patchMail = (req, res) => {
        return res.status(200).json({
            message: 'Add Mail endpoint is not implemented yet'
        });
}

const deleteMail = (req, res) => {
        return res.status(200).json({
            message: 'Add Mail endpoint is not implemented yet'
        });
}

export { getAllMails, getMailById, createMail, patchMail, deleteMail };