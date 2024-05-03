/**
 * Copyright (c) 2010-2023 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.apsystemsez1.internal;

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * The {@link APsystemsEZ1Configuration} class contains fields mapping thing configuration parameters.
 *
 * @author Tom David Eibich - Initial contribution
 */
@NonNullByDefault
public class APsystemsEZ1Configuration {

    /**
     * Sample configuration parameters. Replace with your own.
     */
    public String hostname = "";
    public int port = 8050;
    public int refreshIntervalSeconds = 10;
}
