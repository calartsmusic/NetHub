#!/bin/bash

mkdir /Applications/SuperCollider/SuperCollider.app/Contents/Resources/SCClassLibrary/CalArts
cp ./NetHub.sc /Applications/SuperCollider/SuperCollider.app/Contents/Resources/SCClassLibrary/CalArts/
echo "NetHub copied into SC Class Library (if you see any errors above about a file existing, ignore them)"
