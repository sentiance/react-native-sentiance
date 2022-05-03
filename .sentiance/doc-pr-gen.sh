#!/usr/bin/env bash
### Sentiance document pr creation script ###

set -e
echo "$SENTIANCE_GITHUB_TOKEN"
rm -rf .docs
git clone https://sentianceuser:$SENTIANCE_GITHUB_TOKEN@github.com/sentiance/documentation.git .docs

# other variables
changedFiles=()
has_file_changed=0

sudo apt-get update && sudo apt-get install jq

spec_maps=$(jq '.mappings' .sentiance/doc-spec.json)
spec_length=$(echo $spec_maps | jq '. | length')

echo "spec_maps: ${spec_maps} \nspec_length: ${spec_length}\nThe rest will be tested over SSH..."

if [[ $spec_length? == "0" ]]; then
    echo "No mappings are configured in .sentiance/doc-spec.json file"
    exit 1
fi

for ((x=0; x<spec_length; x++));
do
    filename=$(echo "$spec_maps" | jq -r ".[$x].fileName")
    repoDirPath=$(echo "$spec_maps" | jq -r ".[$x].repoDirPath")
    docFilePath=$(echo "$spec_maps" | jq -r ".[$x].docFilePath")

    if [ ! -f "$repoDirPath/$filename" ]; then
    echo "Repo file does not exist."
    exit 2
    fi

    if [ ! -f ".docs/$docFilePath/$filename" ]; then
    echo "Doc file does not exist."
    exit 3
    fi

    is_different="0"
    diff -q "$repoDirPath/$filename" ".docs/$docFilePath/$filename" || is_different="1"
    if [[ $is_different == "0" ]]
    then
    echo "No changes to $filename"
    else
    echo "$filename has changes, so updating..."
    cp $repoDirPath/$filename .docs/$docFilePath/$filename
    has_file_changed=1
    changedFiles+=( "$filename")
    fi
done

if [[ $has_file_changed == "1" ]]; then
    currentRevision=$(git rev-parse --short HEAD)
    echo "creating PR on docs repo due to changes"

    # install gh cli for PR creation in ubuntu
    curl -fsSL https://cli.github.com/packages/githubcli-archive-keyring.gpg | sudo dd of=/usr/share/keyrings/githubcli-archive-keyring.gpg
    echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/githubcli-archive-keyring.gpg] https://cli.github.com/packages stable main" | sudo tee /etc/apt/sources.list.d/github-cli.list > /dev/null
    sudo apt update
    sudo apt install gh

    cd .docs
    dateFormat=$(date +%F)
    git checkout -b patch/$CIRCLE_PROJECT_REPONAME-$dateFormat-$currentRevision
    git add --all

    echo "Changes were done on ${changedFiles[*]} by user $CIRCLE_USERNAME"

    git config user.name "sentianceuser"
    git config user.email "sre@sentiance.com"
    #git config 'credential.https://github.com' '!gh auth git-credential'
    git commit -m "Changes were done on ${changedFiles[*]} by user $CIRCLE_USERNAME"

    # Let's create the PR
    prTitle="Update from ${CIRCLE_PROJECT_REPONAME} by ${CIRCLE_USERNAME} at commit: $currentRevision"
    prBody="Changed files ${changedFiles[*]}"
    echo "$SENTIANCE_GITHUB_TOKEN" | gh auth login --with-token
    gh alias set --shell cpr "git push -u origin HEAD && gh pr create -t '$prTitle' -b '$prBody' -a @me -B master --fill -r $GITHUB_REVIEWERS"
    gh cpr
fi