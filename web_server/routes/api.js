import express from 'express';
import usersRouter from './api/users.js';
import mailsRouter from './api/mails.js';
import labelsRouter from './api/labels.js';
import tokensRouter from './api/tokens.js';
import blacklistRouter from './api/blacklist.js';
import searchRouter from './api/search.js';
const router = express.Router();
// Define the API routes
router.use('/users', usersRouter);
router.use('/mails', mailsRouter);
router.use('/labels', labelsRouter);
router.use('/tokens', tokensRouter);
router.use('/blacklist', blacklistRouter);
router.use('/search', searchRouter);
// Catch-all route for undefined API endpoints
router.use((req, res) => {
  res.status(404).json({ error: 'API endpoint not found' });
});
export default router;