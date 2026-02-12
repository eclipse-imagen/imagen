/* JAI-Ext - OpenSource Java Advanced Image Extensions Library
*    http://www.geo-solutions.it/
*    Copyright 2018 GeoSolutions
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at

* http://www.apache.org/licenses/LICENSE-2.0

* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
* SPDX-License-Identifier: Apache-2.0
*/
/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2016, Open Source Geospatial Foundation (OSGeo)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 * SPDX-License-Identifier: LGPL-2.1
 */
package org.eclipse.imagen.media.classbreaks;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

/**
 * This class gathers method for percentages' computation for natural breaks, quantile and equal interval
 * classifications
 */
class ClassPercentagesManager {

    // Quantile
    double[] getPercentages(Map<Double, Integer> data, List<Double> breaks, double nValues, int numClasses) {
        double[] percentages = new double[numClasses];
        Set<Double> keys = data.keySet();
        for (int i = 0; i < numClasses; i++) {
            double current = breaks.get(i);
            double next = breaks.get(i + 1);
            boolean last = numClasses == i + 1;
            double classMembers;
            if (last) {
                classMembers =
                        getClassMembersCount(data, keys, v -> v.doubleValue() >= current && v.doubleValue() <= next);
            } else {
                classMembers =
                        getClassMembersCount(data, keys, v -> v.doubleValue() >= current && v.doubleValue() < next);
            }
            percentages[i] = (classMembers / nValues) * 100;
        }
        return percentages;
    }

    private int getClassMembersCount(Map<Double, Integer> data, Set<Double> keys, Predicate<Double> predicate) {
        int classMembersCount = 0;
        for (Double key : keys) {
            if (predicate.test(key)) {
                classMembersCount += data.get(key);
            }
        }
        return classMembersCount;
    }

    // Natural Breaks and Equal Interval
    double[] getPercentages(List<Double> data, Double[] breaks, double totalSize, int numClasses) {
        double[] percentages = new double[numClasses];
        for (int i = 0; i < numClasses; i++) {
            double current = breaks[i];
            double next = breaks[i + 1];
            boolean last = numClasses == i + 1;
            double classMembers;
            if (last) {
                classMembers = getClassMembersCount(v -> v.doubleValue() >= current && v.doubleValue() <= next, data);
            } else {
                classMembers = getClassMembersCount(v -> v.doubleValue() >= current && v.doubleValue() < next, data);
            }
            percentages[i] = (classMembers / totalSize) * 100;
        }
        return percentages;
    }

    private double getClassMembersCount(Predicate<Double> predicate, List<Double> values) {
        int classMembers = 0;
        for (int i = 0; i < values.size(); i++) {
            if (predicate.test(values.get(i))) {
                classMembers++;
            }
        }
        return classMembers;
    }

    // histogram natural breaks and quantile
    double[] getPercentages(List<HistogramClassification.Bucket> buckets, List<Double> breaks, int numClasses) {
        double[] percentages = new double[numClasses];
        double[] arClassMembers = new double[numClasses];
        int totalSize = 0;
        for (int i = 0; i < buckets.size(); i++) {
            HistogramClassification.Bucket b = buckets.get(i);
            totalSize += b.getCount();
            getClassMembersCount(numClasses, breaks, arClassMembers, b);
        }
        for (int i = 0; i < numClasses; i++) {
            percentages[i] = (arClassMembers[i] / totalSize) * 100;
        }
        return percentages;
    }

    private void getClassMembersCount(
            int numClasses, List<Double> breaks, double[] arClassMembers, HistogramClassification.Bucket b) {
        int bucketCount = b.getCount();
        for (int i = 0; i < numClasses; i++) {
            double current = breaks.get(i);
            double next = breaks.get(i + 1);
            boolean last = i + 1 == numClasses;
            Double overlapping;
            if (last) {
                overlapping = getOverlappingPercentages(current, next, b.getMin(), b.getMax());
            } else {
                overlapping = getOverlappingPercentages(current, Math.nextDown(next), b.getMin(), b.getMax());
            }
            if (overlapping.doubleValue() == 1.0) {
                arClassMembers[i] += bucketCount;
            } else if (overlapping.doubleValue() == 0.0) {
                arClassMembers[i] += 0.0;
            } else {
                double classMembers = overlapping * bucketCount;
                arClassMembers[i] += classMembers;
            }
        }
    }

    private Double getOverlappingPercentages(double cMin, double cMax, double bMin, double bMax) {
        if (cMin > bMax || cMax < bMin) {
            return 0.0;
        }
        double regionMin = Math.max(cMin, bMin);
        double regionMax = Math.min(cMax, bMax);
        double dividend = regionMax - regionMin;
        double divider = bMax - bMin;
        if (divider == 0.0) {
            // bucket min and max are equal, handling 0 divider case
            if (dividend == 0.0) {
                return 1.0;
            } else return 0.0;
        } else return dividend / divider;
    }
}
