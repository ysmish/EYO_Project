import express from 'express';
import { createToken } from '../../controllers/tokens.js';
const router = express.Router();

router.route('/')
        .post(createToken);

export default router;