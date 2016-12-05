# com.opsstrattechbazar.rest
Pandora Data Capture

# How to install in ECLIPSE

## One way to do it

1. Clone repository into C:\ECLIPSEWORKSPACE (it will add folder C:\ECLIPSEWORKSPACE\com.datacapture.rest
2. Start up ECLIPSE with working directory C:\ECLIPSEWORKSPACE
3. In ECLIPSE select Import... 
3. Then select "Existing Project Into Workspace", select C:\ECLIPSEWORKSPACE\com.datacapture.rest
4. Create server with Apache Tomcat 8
5. Add Git Perspective

## The other way to do it

1. Start up ECLIPSE with working directory C:\ECLIPSEWORKSPACE
2. Select Import (right click in project explorer)
3. Select Other and write git
4. Select Clone URL
5. Select the location to store the file (does not have to be in eclipse working directory, could be dropbox)
6. Create server with Apache Tomcat 8


#Rule 1
Description for rule 1.

<div style="-webkit-column-count: 2; -moz-column-count: 2; column-count: 2; -webkit-column-rule: 1px dotted #e0e0e0; -moz-column-rule: 1px dotted #e0e0e0; column-rule: 1px dotted #e0e0e0;">
    <div style="display: inline-block;">
        <h2>Good</h2>
        <pre><code class="language-c">int foo (void) 
{
    int i;
}
</code></pre>
    </div>
    <div style="display: inline-block;">
        <h2>Bad</h2>
        <pre><code class="language-c">int foo (void) {
    int i;
}
</code></pre>
    </div>
</div>
