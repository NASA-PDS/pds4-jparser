package gov.nasa.arc.pds.tools.container;

import java.io.File;
import java.io.Serializable;
import gov.nasa.arc.pds.tools.util.FileUtils;

/**
 * A representation of a File object when the file is no longer accessible. For example, an applet
 * may collect information about files on a user's system. Analysis of these files may be necessary
 * once the results are sent back to a server. In order to do so, certain file characteristics need
 * to be burned in (such as whether the file is a directory).
 *
 * @author jagander
 * @version $Revision: $
 *
 */
public class FileMirror implements Serializable, BaseContainerInterface {

  private static final long serialVersionUID = 6829391263115726395L;

  protected final String name;

  protected final Boolean isDirectory;

  protected final String relativePath;

  protected final String parent;

  protected final long length;

  public FileMirror(final File file, final File root) {
    this.name = file.getName();
    this.isDirectory = file.isDirectory();
    this.relativePath = FileUtils.getRelativePath(root, file);
    if (!this.relativePath.equals("")) { //$NON-NLS-1$
      this.parent = FileUtils.getRelativePath(root, file.getParentFile());
    } else {
      this.parent = null;
    }
    this.length = file.length();
  }

  public String getName() {
    return this.name;
  }

  public Boolean isDirectory() {
    return this.isDirectory;
  }

  public String getRelativePath() {
    return this.relativePath;
  }

  public String getParent() {
    return this.parent;
  }

  public long length() {
    return this.length;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    String className = ""; //$NON-NLS-1$
    if (obj != null) {
      className = obj.getClass().getName();
    }
    // TODO: figure out how to best test equality with subclasses
    if ((obj == null) || (!className.contains("FileMirror") && !className //$NON-NLS-1$
        .contains("FileNode"))) { //$NON-NLS-1$
      return false;
    }
    FileMirror node = (FileMirror) obj;
    return this.relativePath.equals(node.getRelativePath())
        && this.isDirectory.equals(node.isDirectory());
  }
}
