// Copyright (c) 2019 Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

import cFile from "./config.json"
import fs from "fs"

const userDefinedFile = getUserDefinedFile()

function getUserDefinedFile() {
    const userConfig = process.env.WEBIDE_PROXY_CONFIG
    if (userConfig) {
        const confFile = userConfig.startsWith('/') ? userConfig : `${__dirname}/${userConfig}`
        if (fs.existsSync(confFile)) {
            console.log("INFO Found user defined config file %s", userConfig)
            return require(userConfig)
        } else {
            console.error("User defined config file %s not found.", userConfig)
        }
    }
    
    return undefined
}

export function read() {
    return userDefinedFile || cFile
}