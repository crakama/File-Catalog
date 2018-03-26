# FileCatalogue

A program that implements a file catalog using Java RMI Communication Paradigm for inter-process communication 
and persists data in a database.

#### Program Requirements:

 - A user can register at the catalog, unregister, login and logout. To be allowed to
upload/download files, a user must be registered and logged in.

- A user specifies username and password when registering at the file catalog server.
On registration, the server verifies that the username is unique. If it is not, the
user is asked to provide another username.

- When logging in to the server, a user provides username and password. The server
verifies the specified username and password.

- A user can upload a local file to the catalog and download files from the catalog
to the local file system. A file at the catalog is identified by its name, thus there
can not be two files with the same name. A file has the following attributes: name;
size; owner; public/private access permission that indicates whether it’s a public
or private file; write and read permissions if the file is public.

- A private file can be retrieved, deleted or updated only by its owner. A public
file can be accessed by any user registered at the file catalog. The write and read
permissions for a public file indicates whether the file is read-only for other users
than the owner or if it can be modified, i.e. deleted or updated, by any user.
Owners have all permissions for their files.

- Users can inspect what files are available in the file catalog, i.e. list the files in the
catalog and their attributes. Note that if a file is marked as private, it can be listed
only by its owner.

- A user can request to be notified when other users access one of its public files.
  The user tells the server for which files it wants to be notified. When one of those
  public files has been read or updated by another user, the server tells the owner
  who performed the action, and what action was taken. It is sufficient that this
  works as long as the user remains online as described by the following scenario.
  A user requests notification, a file is accessed, that user is notified, the user goes
  offline, a file is accessed, the user is not notified, the user comes online again, a file
  is accessed, the user is still not notified.
The files in the catalog are stored on the server’s file system under a specified directory(all files in a directory
                                                                                            dedicated to that purpose).