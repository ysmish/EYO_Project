import { createNewMail, extractUrls } from '../models/mails.js';
import { checkUrl } from '../models/blacklist.js';
import { getUser } from '../models/users.js';

const getAllMails = (req, res) => {
    return res.status(200).json({
        message: 'Add Mail endpoint is not implemented yet'
    });
}

const getMailById = (req, res) => {
    return res.status(200).json({
        message: 'Add Mail endpoint is not implemented yet'
    });
}

const createMail = (req, res) => {
        const username = req.headers.authorization;
        
        if (!username) {
            return res.status(400).json({error: 'Username is required' });
        }

        const { to, subject, body, attachments } = req.body;
        const cc = req.body.cc || [];

        if (getUser(username).error || getUser(to).error) {
            return res.status(400).json({error: 'User not found' });
        }
        
        for (const user of cc) {
            if (getUser(user).error) {
                return res.status(400).json({error: 'User not found' });
            }
        }

        // Basic validation
        if (!to || !subject || !body) {
            return res.status(400).json({error: 'Missing required fields' });
        }

        // Extract URLs from the body
        const urls = extractUrls(body);

        // Check all URLs against the URL server
        try {
            for (const url of urls) {
                const isAllowed = checkUrl(url);
                if (!isAllowed) {
                    return res.status(400).json({ error: `URL ${url} is blacklisted` });
                }
            }
        } catch (error) {
            console.error('Error checking URLs:', error);
            return res.status(500).json({ error: 'Failed to validate URLs' });
        }

        // Create the mail if all URLs are valid
        const newMail = createNewMail(
            username,
            to,
            cc,
            subject,
            body,
            attachments || []
        );

        return res.status(201).location(`/mails/${newMail.id}`).json(newMail);
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