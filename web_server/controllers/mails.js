import { getLatestMails, createNewMail, extractUrls } from '../models/mails.js';
import { checkUrl } from '../utils/urlChecker.js';

const getAllMails = (req, res) => {
    try {
        const username = req.headers.authorization;
        
        if (!username) {
            return res.status(400).json({error: 'Username is required.'});
        }

        const mails = getLatestMails(username);
        return res.status(200).json({mails});
    } catch (error) {
        console.error('Error fetching mails:', error);
        return res.status(500).json({error: 'Failed to fetch mails' });
    }
}

const getMailById = (req, res) => {
    return res.status(200).json({
        message: 'Add Mail endpoint is not implemented yet'
    });
}

const createMail = async (req, res) => {
    try {
        const username = req.headers.authorization;
        
        if (!username) {
            return res.status(401).json({error: 'Authorization header is required' });
        }

        const { from, to, cc, subject, body } = req.body;

        // Basic validation
        if (!from || !to || !subject || !body) {
            return res.status(400).json({error: 'Missing required fields' });
        }

        // Validate that the user is sending from their own email
        if (from !== username) {
            return res.status(400).json({error: 'Username is required.' });
        }

        // Extract URLs from the body
        const urls = extractUrls(body);

        // Check all URLs against the URL server
        try {
            for (const url of urls) {
                const isAllowed = await checkUrl(url);
                if (!isAllowed) {
                    return res.status(400).json({ error: `URL ${url} is blacklisted` });
                }
            }
        } catch (error) {
            console.error('Error checking URLs:', error);
            return res.status(500).json({ error: 'Failed to validate URLs' });
        }

        // Create the mail if all URLs are valid
        const newMail = createNewMail({
            from,
            to,
            cc: cc || [],
            subject,
            body
        });

        return res.status(201).json({newMail});
    } catch (error) {
        console.error('Error creating mail:', error);
        return res.status(500).json({error: 'Failed to create mail' });
    }
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