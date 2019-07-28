/*
 * Copyright 2011 Victor Cordis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Please contact the author ( cordis.victor@gmail.com ) if you need additional
 * information or have any questions.
 */
package net.sourceforge.easycsp.sample.jobscheduling;

public class Job {

    private int id;
    private int operationCount;


    public Job(int id, int operationcount){
        this.id            = id;
        this.operationCount= operationcount;
    }

    public int getId(){
        return this.id;
    }

    public int getOperationCount(){
        return this.operationCount;
    }

    @Override
    public boolean equals(Object o){
        if( o instanceof Job){
            Job j=(Job) o;
            return this.id == j.id;
        }
        return false;
    }

    @Override
    public String toString(){
        return "J(" + this.id + "," + this.operationCount + ")";
    }

}//class Job.
