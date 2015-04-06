/*
 *   Copyright 2015 Kirby Banman,
 *                  Stuart Bildfell,
 *                  Elliot Colp,
 *                  Christian Ellinger,
 *                  Braedy Kuzma,
 *                  Ryan Thornhill
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package cmput301w15t07.TravelTracker.util;

import java.io.FileNotFoundException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.google.gson.reflect.TypeToken;

import android.content.Context;
import android.util.Log;

/**
 * A list whose contents will persist on the filesystem.
 * 
 * @author kdbanman
 *
 * @param <T> The type contained by the list.
 */
public class PersistentList<T> implements List<T> {
	
	private ArrayList<T> list;
	private String filename;
	private Type wrappedClass;
	
	private Context ctx;
	
	/**
	 * Creates a list using the supplied filename and application context.
	 * If the file exists, the list is populated with the contents.  Otherwise a new one
	 * is created.  The file is updated every time the list changes.
	 * @param filename
	 * @param ctx
	 */
	public PersistentList(String filename, Context ctx, Type clazz) {
		this.filename = filename;
		this.ctx = ctx;
		this.wrappedClass = clazz;
		
		loadList();
	}

	@Override
	public boolean add(T t) {
		boolean ret = list.add(t);
		saveList();
		return ret;
	}

	@Override
	public boolean addAll(Collection<? extends T> t) {
		boolean ret = list.addAll(t);
		saveList();
		return ret;
	}

	@Override
	public void clear() {
		list.clear();
		saveList();
	}

	@Override
	public boolean contains(Object o) {
		return list.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> o) {
		return list.containsAll(o);
	}

	@Override
	public T get(int index) {
		return list.get(index);
	}

	@Override
	public int indexOf(Object o) {
		return list.indexOf(o);
	}

	@Override
	public boolean isEmpty() {
		return list.isEmpty();
	}

	@Override
	public Iterator<T> iterator() {
		return list.iterator();
	}

	@Override
	public boolean remove(Object object) {
		boolean ret = list.remove(object);
		saveList();
		return ret;
	}

	@Override
	public boolean removeAll(Collection<?> collection) {
		boolean ret = list.removeAll(collection);
		saveList();
		return ret;
	}

	@Override
	public boolean retainAll(Collection<?> collection) {
		boolean ret = list.retainAll(collection);
		saveList();
		return ret;
	}

	@Override
	public int size() {
		return list.size();
	}

	@Override
	public Object[] toArray() {
		return list.toArray();
	}

	@Override
	public <T> T[] toArray(T[] array) {
		return list.<T>toArray(array);
	}

	@Override
	public void add(int location, T object) {
		list.add(location, object);
		saveList();
	}

	@Override
	public boolean addAll(int location, Collection<? extends T> collection) {
		boolean ret = list.addAll(location, collection);
		saveList();
		return ret;
	}

	@Override
	public int lastIndexOf(Object object) {
		return list.lastIndexOf(object);
	}

	@Override
	public ListIterator<T> listIterator() {
		return list.listIterator();
	}

	@Override
	public ListIterator<T> listIterator(int location) {
		return list.listIterator(location);
	}

	@Override
	public T remove(int location) {
		T ret = list.remove(location);
		saveList();
		return ret;
	}

	@Override
	public T set(int location, T object) {
		T ret = list.set(location, object);
		saveList();
		return ret;
	}

	@Override
	public List<T> subList(int start, int end) {
		return list.subList(start, end);
	}
	
	private void loadList() {
		list = null;
		GsonIOManager gson = new GsonIOManager(ctx);
		try {
			list = gson.<ArrayList<T>>load(filename, new ArrayListType());
		} catch (FileNotFoundException e) {
			Log.i("PersistentList", "file not found - creating empty persistent list");
			list = new ArrayList<T>();
		}
	}
	
	private void saveList() {
		GsonIOManager gson = new GsonIOManager(ctx);
//		gson.save(list, filename, new ArrayListType());
        gson.save(list, filename, new TypeToken<ArrayList<T>>() {}.getType());
	}

	/*
	 * Three methods below are courtesy of
	 * 	http://pastebin.com/PV6cP9jc
	 * on 4 April 2015
	 */
	public class ArrayListType implements ParameterizedType {
		
		@Override
		public Type[] getActualTypeArguments() {
			return new Type[] {wrappedClass};
		}
	
		@Override
		public Type getOwnerType() {
			return null;
		}
	
		@Override
		public Type getRawType() {
			return ArrayList.class;
		}
	}
}
