#!/bin/bash

TAG_NAME="$1"
REMOTE_NAME="origin"

if [[ -z "$TAG_NAME" ]]; then
    echo "Usage: $0 <tag-name>"
    exit 1
fi

echo "Extracting message from tag '$TAG_NAME'"
TAG_MESSAGE=$(git for-each-ref --format='%(contents)' refs/tags/$TAG_NAME)
if [ $? -ne 0 ] || [ -z "$TAG_MESSAGE" ]; then
    echo "Failed to extract message from tag $TAG_NAME"
    exit 1
fi
echo "Extracted message '$TAG_MESSAGE'"

echo "Deleting tag from remote '$REMOTE_NAME'"
git push --delete $REMOTE_NAME $TAG_NAME
if [ $? -ne 0 ]; then
    echo "Failed to remove remote tag $TAG_NAME"
    exit 1
fi

echo "Deleting tag locally"
git tag -d $TAG_NAME
if [ $? -ne 0 ]; then
    echo "Failed to remove local tag $TAG_NAME"
    exit 1
fi

echo "Creating tag locally"
git tag -a $TAG_NAME -m "$TAG_MESSAGE"
if [ $? -ne 0 ]; then
    echo "Failed to create local tag $TAG_NAME with extracted message"
    exit 1
fi

echo "Pushing tag to remote '$REMOTE_NAME'"
git push $REMOTE_NAME $TAG_NAME
if [ $? -ne 0 ]; then
    echo "Failed to push tag $TAG_NAME to remote $REMOTE_NAME"
    exit 1
fi

echo "Tag $TAG_NAME has been successfully recreated with its original message and pushed to $REMOTE_NAME"
