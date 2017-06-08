function jdbc_replace(file)

  Set fsoin = CreateObject("ADODB.Stream")
  fsoin.CharSet = "utf-8"
  fsoin.Open
  fsoin.LoadFromFile(file)
  strData = fsoin.ReadText()
  fsoin.Close
  
  set reAddress = new regexp
  reAddress.Pattern = "jdbc:hive2://[0-9]+.[0-9]+.[0-9]+.[0-9]+:[0-9]+"
  reAddress.Global = true
  reAddress.IgnoreCase = false
  
  resultAddress = reAddress.replace(strData, "jdbc:hive2://" + inceptor)
  
  set reUsername = new regexp
  reUsername.Pattern = "key=""username"" value=""[a-zA-Z0-9]+"""
  reUsername.Global = true
  reUsername.IgnoreCase = false
  
  resultUsername = reUsername.replace(resultAddress, "key=""username"" value=""" + username + """")
  
  set rePassword = new regexp
  rePassword.Pattern = "key=""password"" value=""[a-zA-Z0-9]+\"""
  rePassword.Global = true
  rePassword.IgnoreCase = false
  
  result = rePassword.replace(resultUsername, "key=""password"" value=""" + password + """")
  
  Set objStreamUTF8 = CreateObject("ADODB.Stream")
  set objStreamUTF8NoBOM = CreateObject("ADODB.Stream")

  
  With objStreamUTF8
  .Charset = "UTF-8"
  .Open
  .WriteText result
  .Position = 0
  .SaveToFile file, 2
  .Type     = 1
  .Position = 3
End With

With objStreamUTF8NoBOM
  .Type    = 1
  .Open
  objStreamUTF8.CopyTo objStreamUTF8NoBOM
  .SaveToFile file, 2
End With

end function

Function Recursion(DirectoryPath)
	Set objFSO = CreateObject("Scripting.FileSystemObject")
	If objFSO.FolderExists(DirectoryPath) Then Exit Function
	Call Recursion(objFSO.GetParentFolderName(DirectoryPath))
	objFSO.CreateFolder(DirectoryPath)
End Function

Function visitSubFolders(currentFolderFullPath)  
    Set fso = CreateObject("Scripting.FileSystemObject")  
    Set currentFolder = fso.GetFolder(currentFolderFullPath)  
    Set subFolderSet = currentFolder.SubFolders
	Set files = currentFolder.Files
    For Each subFolder in subFolderSet   
		visitSubFolders(subFolder.path)
    Next
	For Each file in files
	  If LCase(fso.GetExtensionName(file.Name)) = "rmp" Then
		jdbc_replace(file.path)
	  End If
	Next
End Function  

samples = createobject("Scripting.FileSystemObject").GetFile(Wscript.ScriptFullName).ParentFolder.ParentFolder.Path + "\samples"
home = CreateObject("WScript.Shell").ExpandEnvironmentStrings("%USERPROFILE%")
midas_home = home + "\.Midas"
target = midas_home + "\repositories\Local_Repository\"
inceptor = InputBox("Input IP:PORT which is Inceptor address", "Midas")
username = InputBox("Input username of Inceptor", "Midas")
password = InputBox("Input password of Inceptor", "Midas")

dim sfo
set sfo = createobject("Scripting.FileSystemObject")
msgbox "source: " + samples,64,"Midas"
msgbox "destination: " + target,64,"Midas"

if not sfo.fileExists(target) Then
    Recursion(target)
end if
sfo.copyfolder samples, target, true

visitSubFolders(target)

msgbox "Samples Installed",64,"Midas"
