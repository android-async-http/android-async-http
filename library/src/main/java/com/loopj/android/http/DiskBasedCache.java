/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.loopj.android.http;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import android.os.SystemClock;

/**
 * Cache implementation that caches files directly onto the hard disk in the
 * specified directory. The default disk usage size is 5MB, but is configurable.
 */
public class DiskBasedCache implements Cache {

	/** Map of the Key, CacheHeader pairs */
	private final Map<String, CacheHeader> mEntries = new LinkedHashMap<String, CacheHeader>(
			16, .75f, true);

	/** Total amount of space currently used by the cache in bytes. */
	private long mTotalSize = 0;

	/** The root directory to use for the cache. */
	private File mRootDirectory;

	/** The maximum size of the cache in bytes. */
	private int mMaxCacheSizeInBytes;
	/** The cache timet out day defualt 7*/
	
	private  int mCacheTimeOutDay=7;

	/** Default maximum disk usage in bytes. */
	private static final int DEFAULT_DISK_USAGE_BYTES = 5 * 1024 * 1024;


	/** High water mark percentage for the cache */
	private static final float HYSTERESIS_FACTOR = 0.9f;

	/** Magic number for current version of cache file format. */
	private static final int CACHE_MAGIC = 0x20120504;

	private static DiskBasedCache diskCache;

	public static DiskBasedCache init(File rootDir) {
		if (diskCache == null) {
			diskCache = new DiskBasedCache(rootDir);
		}
		return diskCache;

	}

	public static DiskBasedCache init(File rootDir, int maxCacheSize) {
		if (diskCache == null) {
			diskCache = new DiskBasedCache(rootDir, maxCacheSize);
		}
		return diskCache;
	}

	public static DiskBasedCache getCache() {
		if(diskCache==null){
			diskCache = new DiskBasedCache(new File(android.os.Environment.getExternalStorageDirectory()+"/httpCache"));
		}
		return diskCache;
	}

	/**
	 * Constructs an instance of the DiskBasedCache at the specified directory.
	 * 
	 * @param rootDirectory
	 *            The root directory of the cache.
	 * @param maxCacheSizeInBytes
	 *            The maximum size of the cache in bytes.
	 */
	public DiskBasedCache(File rootDirectory, int maxCacheSizeInBytes) {
		mRootDirectory = rootDirectory;
		mMaxCacheSizeInBytes = maxCacheSizeInBytes;
	}
	
	public void setRootDir(File rootDirectory){
		mRootDirectory = rootDirectory;
	}
	/**
	 * set Cache timeout Day 
	 * @param day
	 */
	public void setTimeOutDay(int day){
		mCacheTimeOutDay=day;
	}
	/**
	 * set MaxDiskCache
	 * @param maxCacheSizeInBytes
	 */
	public void setMaxCache(int maxCacheSizeInBytes){
		mMaxCacheSizeInBytes = maxCacheSizeInBytes;
	}
	
	/**
	 * Constructs an instance of the DiskBasedCache at the specified directory
	 * using the default maximum cache size of 5MB.
	 * 
	 * @param rootDirectory
	 *            The root directory of the cache.
	 */
	public DiskBasedCache(File rootDirectory) {
		this(rootDirectory, DEFAULT_DISK_USAGE_BYTES);
	}

	/**
	 * Clears the cache. Deletes all cached files from disk.
	 */
	@Override
	public synchronized void clear() {
		File[] files = mRootDirectory.listFiles();
		if (files != null) {
			for (File file : files) {
				file.delete();
			}
		}
		mEntries.clear();
		mTotalSize = 0;
	}

	/**
	 * Returns the cache entry with the specified key if it exists, null
	 * otherwise.
	 */
	@Override
	public synchronized Entry get(String key) {
		CacheHeader entry = mEntries.get(key);
		// if the entry does not exist, return.
		if (entry == null) {
			return null;
		}

		File file = getFileForKey(key);
		CountingInputStream cis = null;
		try {
			cis = new CountingInputStream(new FileInputStream(file));
			CacheHeader.readHeader(cis); // eat header
			byte[] data = streamToBytes(cis,
					(int) (file.length() - cis.bytesRead));
			return entry.toCacheEntry(data);
		} catch (IOException e) {
			remove(key);
			return null;
		} finally {
			if (cis != null) {
				try {
					cis.close();
				} catch (IOException ioe) {
					return null;
				}
			}
		}
	}

	/**
	 * Initializes the DiskBasedCache by scanning for all files currently in the
	 * specified root directory. Creates the root directory if necessary.
	 */
	@Override
	public synchronized void initialize() {
		if (!mRootDirectory.exists()) {
			if (!mRootDirectory.mkdirs()) {
			}
			return;
		}

		File[] files = mRootDirectory.listFiles();
		if (files == null) {
			return;
		}
		for (File file : files) {
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(file);
				CacheHeader entry = CacheHeader.readHeader(fis);
				entry.size = file.length();
				//if cache saveDate timeOut  delete
				if ((System.currentTimeMillis() - entry.serverDate) / 86400000 > mCacheTimeOutDay) {
					if (file != null) {
						file.delete();
					}
				} else {
					putEntry(entry.key, entry);
				}
			} catch (IOException e) {
				if (file != null) {
					file.delete();
				}
			} finally {
				try {
					if (fis != null) {
						fis.close();
					}
				} catch (IOException ignored) {
				}
			}
		}
	}

//	/**
//	 * Invalidates an entry in the cache.
//	 * 
//	 * @param key
//	 *            Cache key
//	 * @param fullExpire
//	 *            True to fully expire the entry, false to soft expire
//	 */
//	@Override
//	public synchronized void invalidate(String key, boolean fullExpire) {
//		Entry entry = get(key);
//		if (entry != null) {
//			entry.softTtl = 0;
//			if (fullExpire) {
//				entry.ttl = 0;
//			}
//			put(key, entry);
//		}
//
//	}

	/**
	 * Puts the entry with the specified key into the cache.
	 */
	@Override
	public synchronized void put(String key, Entry entry) {
		pruneIfNeeded(entry.data.length);
		File file = getFileForKey(key);
		try {
			FileOutputStream fos = new FileOutputStream(file);
			CacheHeader e = new CacheHeader(key, entry);
			e.writeHeader(fos);
			fos.write(entry.data);
			fos.close();
			putEntry(key, e);
			return;
		} catch (IOException e) {
			e.printStackTrace();
		}
		boolean deleted = file.delete();
		if (!deleted) {
		}
	}

	/**
	 * Removes the specified key from the cache if it exists.
	 */
	@Override
	public synchronized void remove(String key) {
		boolean deleted = getFileForKey(key).delete();
		removeEntry(key);
		if (!deleted) {
		}
	}

	/**
	 * Creates a pseudo-unique filename for the specified cache key.
	 * 
	 * @param key
	 *            The key to generate a file name for.
	 * @return A pseudo-unique filename.
	 */
	private String getFilenameForKey(String key) {
		int firstHalfLength = key.length() / 2;
		String localFilename = String.valueOf(key.substring(0, firstHalfLength)
				.hashCode());
		localFilename += String.valueOf(key.substring(firstHalfLength)
				.hashCode());
		return localFilename;
	}

	/**
	 * Returns a file object for the given cache key.
	 */
	public File getFileForKey(String key) {
		return new File(mRootDirectory, getFilenameForKey(key));
	}

	/**
	 * 每次存储将会教研是否超过本地客户端设置的缓存区域大小，如果超过就删除过期的缓存
	 * 
	 * @param neededSpace
	 *            The amount of bytes we are trying to fit into the cache.
	 */
	private void pruneIfNeeded(int neededSpace) {
		if ((mTotalSize + neededSpace) < mMaxCacheSizeInBytes) {
			return;
		}
		long before = mTotalSize;
		int prunedFiles = 0;
		long startTime = SystemClock.elapsedRealtime();

		Iterator<Map.Entry<String, CacheHeader>> iterator = mEntries.entrySet()
				.iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, CacheHeader> entry = iterator.next();
			CacheHeader e = entry.getValue();
			boolean deleted = getFileForKey(e.key).delete();
			if (deleted) {
				mTotalSize -= e.size;
			} else {
			}
			iterator.remove();
			prunedFiles++;

			if ((mTotalSize + neededSpace) < mMaxCacheSizeInBytes
					* HYSTERESIS_FACTOR) {
				break;
			}
		}

	}

	/**
	 * Puts the entry with the specified key into the cache.
	 * 
	 * @param key
	 *            The key to identify the entry by.
	 * @param entry
	 *            The entry to cache.
	 */
	private void putEntry(String key, CacheHeader entry) {
		if (!mEntries.containsKey(key)) {
			mTotalSize += entry.size;
		} else {
			CacheHeader oldEntry = mEntries.get(key);
			mTotalSize += (entry.size - oldEntry.size);
		}
		mEntries.put(key, entry);
	}

	/**
	 * Removes the entry identified by 'key' from the cache.
	 */
	private void removeEntry(String key) {
		CacheHeader entry = mEntries.get(key);
		if (entry != null) {
			mTotalSize -= entry.size;
			mEntries.remove(key);
		}
	}

	/**
	 * Reads the contents of an InputStream into a byte[].
	 * */
	private static byte[] streamToBytes(InputStream in, int length)
			throws IOException {
		byte[] bytes = new byte[length];
		int count;
		int pos = 0;
		while (pos < length
				&& ((count = in.read(bytes, pos, length - pos)) != -1)) {
			pos += count;
		}
		if (pos != length) {
			throw new IOException("Expected " + length + " bytes, read " + pos
					+ " bytes");
		}
		return bytes;
	}

	/**
	 * Handles holding onto the cache headers for an entry.
	 */
	// Visible for testing.
	static class CacheHeader {
		/**
		 * The size of the data identified by this CacheHeader. (This is not
		 * serialized to disk.
		 */
		public long size;

		/** The key that identifies the cache entry. */
		public String key;

		/** ETag for cache coherence. */
		public String etag;

		/** Date of this response as reported by the server. */
		public long serverDate;

		/** TTL for this record. */
		public long ttl;


		/** Headers from the response resulting in this cache entry. */

		private CacheHeader() {
		}

		/**
		 * Instantiates a new CacheHeader object
		 * 
		 * @param key
		 *            The key that identifies the cache entry
		 * @param entry
		 *            The cache entry.
		 */
		public CacheHeader(String key, Entry entry) {
			this.key = key;
			this.size = entry.data.length;
			this.etag = entry.etag;
			this.serverDate = entry.serverDate;
		}

		/**
		 * Reads the header off of an InputStream and returns a CacheHeader
		 * object.
		 * 
		 * @param is
		 *            The InputStream to read from.
		 * @throws IOException
		 */
		public static CacheHeader readHeader(InputStream is) throws IOException {
			CacheHeader entry = new CacheHeader();
			int magic = readInt(is);
			if (magic != CACHE_MAGIC) {
				// don't bother deleting, it'll get pruned eventually
				throw new IOException();
			}
			entry.key = readString(is);
			entry.etag = readString(is);
			if (entry.etag.equals("")) {
				entry.etag = null;
			}
			entry.serverDate = readLong(is);
			entry.ttl = readLong(is);
			return entry;
		}

		/**
		 * Creates a cache entry for the specified data.
		 */
		public Entry toCacheEntry(byte[] data) {
			Entry e = new Entry();
			e.data = data;
			e.etag = etag;
			e.serverDate = serverDate;
			return e;
		}

		/**
		 * Writes the contents of this CacheHeader to the specified
		 * OutputStream.
		 */
		public boolean writeHeader(OutputStream os) {
			try {
				writeInt(os, CACHE_MAGIC);
				writeString(os, key);
				writeString(os, etag == null ? "" : etag);
				writeLong(os, serverDate);
				writeLong(os, ttl);
				os.flush();
				return true;
			} catch (IOException e) {
				return false;
			}
		}

	}

	private static class CountingInputStream extends FilterInputStream {
		private int bytesRead = 0;

		private CountingInputStream(InputStream in) {
			super(in);
		}

		@Override
		public int read() throws IOException {
			int result = super.read();
			if (result != -1) {
				bytesRead++;
			}
			return result;
		}

		@Override
		public int read(byte[] buffer, int offset, int count)
				throws IOException {
			int result = super.read(buffer, offset, count);
			if (result != -1) {
				bytesRead += result;
			}
			return result;
		}
	}

	/*
	 * Homebrewed simple serialization system used for reading and writing cache
	 * headers on disk. Once upon a time, this used the standard Java
	 * Object{Input,Output}Stream, but the default implementation relies heavily
	 * on reflection (even for standard types) and generates a ton of garbage.
	 */

	/**
	 * Simple wrapper around {@link InputStream#read()} that throws EOFException
	 * instead of returning -1.
	 */
	private static int read(InputStream is) throws IOException {
		int b = is.read();
		if (b == -1) {
			throw new EOFException();
		}
		return b;
	}

	static void writeInt(OutputStream os, int n) throws IOException {
		os.write((n >> 0) & 0xff);
		os.write((n >> 8) & 0xff);
		os.write((n >> 16) & 0xff);
		os.write((n >> 24) & 0xff);
	}

	static int readInt(InputStream is) throws IOException {
		int n = 0;
		n |= (read(is) << 0);
		n |= (read(is) << 8);
		n |= (read(is) << 16);
		n |= (read(is) << 24);
		return n;
	}

	static void writeLong(OutputStream os, long n) throws IOException {
		os.write((byte) (n >>> 0));
		os.write((byte) (n >>> 8));
		os.write((byte) (n >>> 16));
		os.write((byte) (n >>> 24));
		os.write((byte) (n >>> 32));
		os.write((byte) (n >>> 40));
		os.write((byte) (n >>> 48));
		os.write((byte) (n >>> 56));
	}

	static long readLong(InputStream is) throws IOException {
		long n = 0;
		n |= ((read(is) & 0xFFL) << 0);
		n |= ((read(is) & 0xFFL) << 8);
		n |= ((read(is) & 0xFFL) << 16);
		n |= ((read(is) & 0xFFL) << 24);
		n |= ((read(is) & 0xFFL) << 32);
		n |= ((read(is) & 0xFFL) << 40);
		n |= ((read(is) & 0xFFL) << 48);
		n |= ((read(is) & 0xFFL) << 56);
		return n;
	}

	static void writeString(OutputStream os, String s) throws IOException {
		byte[] b = s.getBytes("UTF-8");
		writeLong(os, b.length);
		os.write(b, 0, b.length);
	}

	static String readString(InputStream is) throws IOException {
		int n = (int) readLong(is);
		byte[] b = streamToBytes(is, n);
		return new String(b, "UTF-8");
	}

	static void writeStringStringMap(Map<String, String> map, OutputStream os)
			throws IOException {
		if (map != null) {
			writeInt(os, map.size());
			for (Map.Entry<String, String> entry : map.entrySet()) {
				writeString(os, entry.getKey());
				writeString(os, entry.getValue());
			}
		} else {
			writeInt(os, 0);
		}
	}


}
