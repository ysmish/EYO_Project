import { hasAccessToMail, getMailById } from '../models/mails.js';

const getAllMails = (req, res) => {
    return res.status(200).json({
        message: 'Add Mail endpoint is not implemented yet'
    });
}

const getMailById = (req, res) => {
    const username = req.headers.authorization;
    
    // Check if user is authenticated
    if (!username) {
        return res.status(400).json({error: 'Username is required.'});
    }

    // Get mail ID from URL parameter
    const mailId = parseInt(req.params.id);
    if (isNaN(mailId)) {
        return res.status(400).json({error: 'Invalid mail ID'});
    }

    try {
        const mail = getMailById(mailId, username);
        if (hasAccessToMail(mail, username)) {  
            return res.status(200).json({mail});
        }
        else {
            return res.status(403).json({error: 'You do not have access to this mail'});
        }
    } catch (error) {
        return res.status(404).json({error: 'Mail not found'});
    }
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