import { getLatestMails, createNewMail, extractUrlsFromMail, getMail, deleteMailOfUser, updateMail } from '../models/mails.js';
import { checkUrl } from '../models/blacklist.js';
import { getUser } from '../models/users.js';
import { authorizeToken } from '../models/tokens.js';

const getAllMails = (req, res) => {
    const token = req.headers.authorization;
    if (!token) {
        return res.status(401).json({ error: 'Authorization token is required.' });
    }

    // Verify JWT token
    const authResult = authorizeToken(token);
    if (authResult.error) {
        return res.status(401).json({ error: authResult.error });
    }

    const username = authResult.username;

    try {
        const latestMails = getLatestMails(username);
        return res.status(200).json(latestMails);
    } catch (error) {
        console.error('Error fetching latest mails:', error);
        return res.status(500).json({error: 'Internal server error.' });
    }
}

const getMailById = (req, res) => {
    const token = req.headers.authorization;
    
    // Check if user is authenticated
    if (!token) {
        return res.status(401).json({error: 'Authorization token is required.'});
    }

    // Verify JWT token
    const authResult = authorizeToken(token);
    if (authResult.error) {
        return res.status(401).json({ error: authResult.error });
    }

    const username = authResult.username;

    // Get mail ID from URL parameter
    const mailId = parseInt(req.params.id);
    if (isNaN(mailId)) {
        return res.status(400).json({error: 'Invalid mail ID'});
    }

    const mail = getMail(username, mailId);
        if (!mail) {
            return res.status(404).json({error: 'Mail not found'});
        }
        else {
            return res.status(200).json(mail);
        }
}

const createMail = async (req, res) => {
        // Get token from header
        const token = req.headers.authorization;
        if (!token) {
            return res.status(401).json({error: 'Authorization token is required' });
        }

        // Verify JWT token
        const authResult = authorizeToken(token);
        if (authResult.error) {
            return res.status(401).json({ error: authResult.error });
        }

        const username = authResult.username;

        const { to, subject, body, attachments } = req.body;
        const cc = req.body.cc || [];

        // Ensure 'to' is an array
        const toUsers = Array.isArray(to) ? to : [to];
        
        // Check if users exist
        const senderCheck = getUser(username);
        if (senderCheck.error) {
            return res.status(400).json({error: 'Sender user not found' });
        }
        
        for (const user of toUsers) {
            const userCheck = getUser(user);
            if (userCheck.error) {
                return res.status(400).json({error: `User '${user}' not found` });
            }
        }
        
        for (const user of cc) {
            const userCheck = getUser(user);
            if (userCheck.error) {
                return res.status(400).json({error: `User '${user}' not found` });
            }
        }

        // Basic validation
        if (!toUsers.length || !subject || !body) {
            return res.status(400).json({error: 'Missing required fields' });
        }

        // Extract URLs from all mail fields
        try {
            const urls = extractUrlsFromMail({
                from: username,
                to,
                cc,
                subject,
                body,
                attachments
            });

            // Check all URLs against the URL server
            for (const url of urls) {
                const isBlacklisted = await checkUrl(url);
                if (isBlacklisted) {
                    return res.status(400).json({ 
                        error: `URL ${url} is blacklisted`,
                        message: 'Found blacklisted URL in mail content'
                    });
                }
            }
        } catch (error) {
            console.error('Error checking URLs:', error);
            return res.status(500).json({ error: 'Failed to validate URLs' });
        }

        // Create the mail if all URLs are valid
        const mailId = createNewMail(
            username,
            toUsers,
            cc,
            subject,
            body,
            attachments || []
        );

        return res.status(201).location(`/api/mails/${mailId}`).end();
}


const patchMail = async (req, res) => {
    // Get token from header
    const token = req.headers.authorization;
    if (!token) {
        return res.status(401).json({ error: 'Authorization token is required.' });
    }

    // Verify JWT token
    const authResult = authorizeToken(token);
    if (authResult.error) {
        return res.status(401).json({ error: authResult.error });
    }

    const username = authResult.username;

    // Get mail ID from URL parameter
    const mailId = parseInt(req.params.id);
    if (isNaN(mailId)) {
        return res.status(400).json({ error: 'Invalid mail ID' });
    }

    // Check if the mail exists and belongs to the user
    const mail = getMail(username, mailId);
    if (!mail) {
        return res.status(404).json({ error: 'Mail not found' });
    }

    const updates = req.body;
    
    // Extract URLs from the updates to check against blacklist
    try {
        const urls = extractUrlsFromMail(updates);
        
        // Check all URLs against the URL server
        for (const url of urls) {
            const isBlacklisted = await checkUrl(url);
            if (isBlacklisted) {
                return res.status(400).json({ 
                    error: `URL ${url} is blacklisted`,
                    message: 'Found blacklisted URL in mail content'
                });
            }
        }
    } catch (error) {
        console.error('Error checking URLs:', error);
        return res.status(500).json({ error: 'Failed to validate URLs' });
    }

    // Update the mail
    const success = updateMail(username, mailId, updates);
    if (success) {
        return res.status(204).end();
    } else {
        return res.status(500).json({ error: 'Failed to update mail' });
    }
};

const deleteMail = (req, res) => {
    // Get token from header
    const token = req.headers.authorization;
    if (!token) {
        return res.status(401).json({ error: 'Authorization token is required.' });
    }

    // Verify JWT token
    const authResult = authorizeToken(token);
    if (authResult.error) {
        return res.status(401).json({ error: authResult.error });
    }

    const username = authResult.username;

    // Get mail ID from URL parameter
    const mailId = parseInt(req.params.id);
    if (isNaN(mailId)) {
        return res.status(400).json({ error: 'Invalid mail ID' });
    }

    // Check if the mail exists and belongs to the user
    const mail = getMail(username, mailId);
    if (!mail) {
        return res.status(404).json({ error: 'Mail not found' });
    }

    // Delete the mail
    const success = deleteMailOfUser(username, mailId);
    if (success) {
        return res.status(204).end();
    } else {
        return res.status(500).json({ error: 'Failed to delete mail' });
    }
}

export { getAllMails, getMailById, createMail, patchMail, deleteMail }; 