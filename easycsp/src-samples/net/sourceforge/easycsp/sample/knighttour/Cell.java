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
package net.sourceforge.easycsp.sample.knighttour;

public class Cell {

    public final int x;
    public final int y;


    public Cell(){
        this.x= -1;
        this.y= -1;
    }

    public Cell(int x, int y){
        this.x= x;
        this.y= y;
    }

    @Override
    public boolean equals(Object o) {
        if( o instanceof Cell){
            Cell c=(Cell) o;
            return this.x == c.x && this.y == c.y;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + this.x;
        hash = 59 * hash + this.y;
        return hash;
    }

    @Override
    public String toString() {
        return "[" + this.x + "," + this.y + "]";
    }

}//class Cell.
