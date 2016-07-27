package com.duowan.game.domain;

import java.io.Serializable;
import java.util.Date;

public class PyServletDto implements Serializable {
	private static final long serialVersionUID = -4281977716813027706L;
	private long id;
	private String className;
	private String requestPath;
	private String script;
	private Date lastModifiedTime;
	/**
	 * -1 for disable, 0 for runnable
	 */
	private int status;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	public Date getLastModifiedTime() {
		return lastModifiedTime;
	}

	public void setLastModifiedTime(Date lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getRequestPath() {
		return requestPath;
	}

	public void setRequestPath(String requestPath) {
		this.requestPath = requestPath;
	}

	private volatile int hashCode = 0;

	@Override
	public int hashCode() {
		int result = hashCode;
		if (result == 0) {
			result = 17;
			result = 31 * result + (this.className == null ? 0 : this.className.hashCode());
			result = 31 * result + (this.script == null ? 0 : this.script.hashCode());
			result = 31 * result
					+ (this.lastModifiedTime == null ? 0 : this.lastModifiedTime.hashCode());
			result = 31 * result + (this.requestPath == null ? 0 : this.requestPath.hashCode());
			result = 31 * result + this.status;
			hashCode = result;
		}
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof PyServletDto)) {
			return false;
		}
		PyServletDto that = (PyServletDto) obj;
		if ((this.className != null && !this.className.equals(that.className))
				|| (this.className == null && that.className != null)
				|| (this.requestPath != null && !this.script.equals(that.requestPath))
				|| (this.requestPath == null && that.requestPath != null)
				|| (this.script != null && !this.script.equals(that.script))
				|| (this.script == null && that.script != null)
				|| (this.lastModifiedTime != null && !(this.lastModifiedTime.getTime() == that.lastModifiedTime
						.getTime()))
				|| (this.lastModifiedTime == null && that.lastModifiedTime != null)
				|| (this.status != that.status)) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public String toString() {
		return String
				.format("id: %d,\r\nclassName: %s,requestPath: %s,\r\nscript: %s,\r\nlastModifiedTime: %d,\r\nstatus: %d",
						this.getId(), this.getClassName(), this.getRequestPath(), this.getScript(),
						this.getLastModifiedTime().getTime(), this.getStatus());
	}

}
