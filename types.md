## Functions

<dl>
<dt><a href="#createUser">createUser(configuration)</a></dt>
<dd><p>CreateUser setups the credentials for the SDK and initializes the SDK.</p>
<p>The application is expected to call &quot;createUser&quot; at the time when it requires
the detections to start. However, it is expected to explicity call &quot;start&quot;
after the &quot;createUser&quot; method</p>
</dd>
<dt><a href="#clear">clear()</a></dt>
<dd><p>Clears the state variables and resets the SDK managed by the &quot;createUser&quot;</p>
<p>This method is intended to be used when a user is logged out or whenever the
SDK is meant to be reset.</p>
</dd>
</dl>

## Typedefs

<dl>
<dt><a href="#CreateUserConfiguration">CreateUserConfiguration</a> : <code>Object</code></dt>
<dd></dd>
</dl>

<a name="createUser"></a>

## createUser(configuration)
CreateUser setups the credentials for the SDK and initializes the SDK.

The application is expected to call "createUser" at the time when it requires
the detections to start. However, it is expected to explicity call "start"
after the "createUser" method

**Kind**: global function  

| Param | Type |
| --- | --- |
| configuration | [<code>CreateUserConfiguration</code>](#CreateUserConfiguration) | 

**Example**  
```js
await RNSentiance.createUser({ credentials: ..., linker: ...});
  await RNSentiance.start()
```
<a name="clear"></a>

## clear()
Clears the state variables and resets the SDK managed by the "createUser"

This method is intended to be used when a user is logged out or whenever the
SDK is meant to be reset.

**Kind**: global function  
<a name="CreateUserConfiguration"></a>

## CreateUserConfiguration : <code>Object</code>
**Kind**: global typedef  
**Properties**

| Name | Type | Description |
| --- | --- | --- |
| credentials | <code>Object</code> | Sentiance APP Credentials |
| credentials.appId | <code>String</code> | APP ID |
| credentials.appSecret | <code>String</code> | APP Secret |
| credentials.baseUrl | <code>String</code> | Sentiance Base URL |
| linker | <code>function</code> | Function to handle the user linking |

