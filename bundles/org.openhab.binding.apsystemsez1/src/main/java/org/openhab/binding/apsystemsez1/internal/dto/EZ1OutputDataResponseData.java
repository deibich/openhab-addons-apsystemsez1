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
package org.openhab.binding.apsystemsez1.internal.dto;

/**
 * @author Tom David Eibich - Initial contribution
 *
 */
public class EZ1OutputDataResponseData extends EZ1ResponseData {
    public Data data;

    public static class Data {
        public float p1;
        public float e1;
        public float te1;
        public float p2;
        public float e2;
        public float te2;
    }
}
