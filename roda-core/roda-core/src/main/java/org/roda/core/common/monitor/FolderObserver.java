/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE file at the root of the source
 * tree and available online at
 *
 * https://github.com/keeps/roda
 */
package org.roda.core.common.monitor;

import java.nio.file.Path;
import java.util.Date;

public interface FolderObserver {
  public void pathAdded(Path basePath, Path createdPath,boolean commit);
  
  public void pathAdded(Path basePath, Path createdPath);

  public void pathModified(Path basePath, Path createdPath);

  public void pathDeleted(Path basePath, Path createdPath);
  
  public void reindex(Path basePath, Date from);
}
