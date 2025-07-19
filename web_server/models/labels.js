import Label from '../service/Label.js';
import Mail from '../service/Mail.js';

let labelIdCounter = 1;  // Counter for generating unique label IDs

const createNewLabel = async (username, name, color) => {
    if (!name) {
        throw { error: 'Label name is required' };
    }

    try {
        // Check if label name already exists for this user
        const existingLabel = await Label.findOne({ username, name });
        if (existingLabel) {
            return { error: 'Label name already exists' };
        }

        // Find the highest labelId for this user and increment
        const highestLabel = await Label.findOne({ username }).sort({ id: -1 });
        const newId = highestLabel ? highestLabel.id + 1 : labelIdCounter++;

        // Create new label
        const newLabel = new Label({
            id: newId,
            name: name,
            username: username,
            color: color || '#4F46E5'
        });

        const savedLabel = await newLabel.save();
        return { id: savedLabel.id, name: savedLabel.name, color: savedLabel.color };
    } catch (error) {
        return { error: 'Database error: ' + error.message };
    }
};

const getLabel = async (username, labelId) => {
    try {
        labelId = parseInt(labelId);
        if (isNaN(labelId)) {
            return { error: 'Invalid label ID' };
        }
        
        // Exclude default labels (1-5)
        if (labelId >= 1 && labelId <= 5) {
            return null;
        }
        
        const label = await Label.findOne({ username, id: labelId });
        if (!label) {
            return null;
        }
        
        return {
            id: label.id,
            name: label.name,
            color: label.color
        };
    } catch (error) {
        return { error: 'Database error: ' + error.message };
    }
};

const getLabels = async (username) => {
    try {
        // Exclude default labels (1-5)
        const labels = await Label.find({ 
            username, 
        });
        return labels.map(label => ({
            id: label.id,
            name: label.name,
            color: label.color
        }));
    } catch (error) {
        return [];
    }
};

const changeLabel = async (username, labelId, updates) => {
    try {
        labelId = parseInt(labelId);
        if (isNaN(labelId)) {
            return { error: 'Invalid label ID' };
        }

        const label = await Label.findOne({ username, id: labelId });
        if (!label) {
            return { error: 'Label not found' };
        }

        if (!updates.name) {
            return { error: 'New label name is required' };
        }

        // Check if the new name already exists (except for this label)
        const existingLabel = await Label.findOne({ 
            username, 
            name: updates.name, 
            id: { $ne: labelId } 
        });
        if (existingLabel) {
            return { error: 'Label name already exists' };
        }

        // Update the label name and color if provided
        label.name = updates.name;
        if (updates.color) {
            label.color = updates.color;
        }
        
        const savedLabel = await label.save();
        return { 
            id: savedLabel.id,
            name: savedLabel.name,
            color: savedLabel.color
        };
    } catch (error) {
        return { error: 'Database error: ' + error.message };
    }
};

const removeLabel = async (username, labelId) => {
    try {
        labelId = parseInt(labelId);
        if (isNaN(labelId) || labelId < 6) { // Ensure labelId is valid and not a default label
            return false;
        }

        const label = await Label.findOne({ username, id: labelId });
        if (!label) {
            return false;
        }

        // Remove this label from mails that use it
        await Mail.updateMany(
            { owner: username, labels: labelId.toString() },
            { $pull: { labels: labelId.toString() } }
        );

        // Delete the label
        await Label.deleteOne({ username, id: labelId });
        return true;
    } catch (error) {
        return false;
    }
};

export { createNewLabel, getLabel, getLabels, changeLabel, removeLabel };