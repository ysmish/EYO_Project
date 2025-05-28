import express from 'express';
import { getAllLabels, getLabelById, createLabel, patchLabel, deleteLabel } from '../../controllers/labels.js';
const router = express.Router();

router.route('/')
        .get(getAllLabels)
        .post(createLabel);
router.route('/:id')
        .get(getLabelById)
        .patch(patchLabel)
        .delete(deleteLabel);

export default router;