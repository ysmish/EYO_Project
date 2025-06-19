import express from 'express';
import { createToken, validateToken } from '../../controllers/tokens.js';
const router = express.Router();

router.route('/')
        .post(createToken);

router.route('/validate')
        .post(validateToken);

export default router;