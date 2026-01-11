const API = 'http://localhost:8080/nodes';

async function loadTree() {
    const res = await fetch(API);
    const data = await res.json();
    const root = data;
    const container = document.getElementById('tree');
    container.innerHTML = '';
    const ul = document.createElement('ul');
    ul.appendChild(renderNode(root));
    container.appendChild(ul);
}

function renderNode(node) {
    const li = document.createElement('li');

    if (node.id === 1) {
        li.className = 'root-node';
    }

    const labelSpan = document.createElement('span');
    labelSpan.className = 'node-label';
    labelSpan.textContent = `${node.title}`;
    li.appendChild(labelSpan);

    const buttonContainer = document.createElement('div');
    buttonContainer.className = 'node-buttons';

    const deleteBtn = document.createElement('button');
    deleteBtn.className = 'node-btn delete-btn';
    deleteBtn.textContent = 'X';
    deleteBtn.onclick = () => deleteNode(node.id);
    buttonContainer.appendChild(deleteBtn);

    const editBtn = document.createElement('button');
    editBtn.className = 'node-btn add-child-btn';
    editBtn.textContent = '✎';
    editBtn.title = 'Edit';
    editBtn.onclick = () => showEditForm(node.id, node.title);
    buttonContainer.appendChild(editBtn);

    // Hide delete button for root node
    // if (node.id === 1) {
    //     deleteBtn.style.display = 'none';
    // }

    const addBtn = document.createElement('button');
    addBtn.className = 'node-btn add-child-btn';
    addBtn.textContent = 'Add Child';
    addBtn.onclick = () => showCreateFormForParent(node.id);
    buttonContainer.appendChild(addBtn);

    const moveBtn = document.createElement('button');
    moveBtn.className = 'node-btn move-btn';
    moveBtn.textContent = 'Change Parent';
    moveBtn.onclick = () => moveNode(node.id);
    buttonContainer.appendChild(moveBtn);

    const reorderBtn = document.createElement('button');
    reorderBtn.className = 'node-btn reorder-btn';
    reorderBtn.textContent = 'Reorder';
    reorderBtn.onclick = () => reorderNode(node.id);
    buttonContainer.appendChild(reorderBtn);

    if (node.parentNodeId === null) {
        moveBtn.style.display = 'none';
    }

    li.appendChild(buttonContainer);

    if (node.children && node.children.length > 0) {
        const ul = document.createElement('ul');
        const sortedChildren = [...node.children].sort((a, b) => (a.sortOrder || 0) - (b.sortOrder || 0));
        sortedChildren.forEach(child => {
            fetchAndRenderChild(ul, child.id);
        });
        li.appendChild(ul);
    }

    return li;
}

async function fetchAndRenderChild(parent, childId) {
    const res = await fetch(`${API}/${childId}`);
    const child = await res.json();
    parent.appendChild(renderNode(child));
}

function showCreateForm() {
    document.getElementById('form').style.display = 'block';
    document.getElementById('parentId').value = '';
}

function showCreateFormForParent(parentId) {
    document.getElementById('form').style.display = 'block';
    document.getElementById('parentId').value = parentId;
    document.getElementById('title').focus();
}

function hideCreateForm() {
    document.getElementById('form').style.display = 'none';
    document.getElementById('title').value = '';
    document.getElementById('parentId').value = '';
}

async function createNode() {
    const title = document.getElementById('title').value;
    const parentId = document.getElementById('parentId').value;

    if (!title) {
        alert('Enter a title');
        return;
    }

    if (parentId !== '') {
        const parentIdNum = Number(parentId);

        if (parentIdNum <= 0) {
            alert('Parent ID must be greater than 0');
            return;
        }

        try {
            const res = await fetch(`${API}/${parentIdNum}`);
            if (!res.ok) {
                alert(`Node with ID ${parentIdNum} does not exist`);
                return;
            }
        } catch (error) {
            alert('Error validating parent node');
            return;
        }
    }

    await fetch(API, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            title: title,
            parentNodeId: parentId ? Number(parentId) : null
        })
    });

    hideCreateForm();
    loadTree();
}

async function deleteNode(id) {
    if (id === 1) {
        alert('You can not delete root node!');
        return;
    }

    if (!confirm('Are you sure you want to delete this node?')) {
        return;
    }

    await fetch(`${API}/${id}`, {
        method: 'DELETE'
    });

    loadTree();
}

async function moveNode(id) {
    const newParentId = prompt("Enter new parent id (null for root):");

    if (newParentId === null) return;

    await fetch(`${API}/${id}/move`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            newParentId: newParentId === "" ? null : Number(newParentId)
        })
    });

    loadTree();
}

async function reorderNode(id) {
    const direction = prompt("Enter direction (UP or DOWN):");

    if (direction === null) return;

    await fetch(`${API}/${id}/reorder`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            direction: direction.toUpperCase()
        })
    });

    loadTree();
}

function showEditForm(nodeId, currentTitle) {
    document.getElementById('editForm').style.display = 'block';
    document.getElementById('editTitle').value = currentTitle;
    document.getElementById('editForm').setAttribute('data-node-id', nodeId);
    document.getElementById('editTitle').focus();
}

function hideEditForm() {
    document.getElementById('editForm').style.display = 'none';
    document.getElementById('editTitle').value = '';
    document.getElementById('editForm').removeAttribute('data-node-id');
}

async function updateNode() {
    const nodeId = document.getElementById('editForm').getAttribute('data-node-id');
    const newTitle = document.getElementById('editTitle').value;

    if (!newTitle) {
        alert('Title cannot be empty');
        return;
    }

    if (nodeId === '1') {
        alert('You cannot edit root node title');
        return;
    }

    try {
        const res = await fetch(`${API}/${nodeId}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                title: newTitle.trim()
            })
        });

        if (!res.ok) {
            alert('Error updating node');
            return;
        }

        hideEditForm();
        loadTree();
    } catch (error) {
        alert('Error updating node');
        console.error(error);
    }
}

loadTree();
