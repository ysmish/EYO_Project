import express from 'express';
import { searchQuery } from '../../controllers/search.js';
const router = express.Router();

router.route('/:query')
        .get(searchQuery);

export default router;