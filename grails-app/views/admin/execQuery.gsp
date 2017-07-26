<%--
  Created by IntelliJ IDEA.
  User: rkrempel
  Date: 30.08.16
  Time: 10:10
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title></title>
    <meta name="layout" content="main">
</head>

<body>
<sec:ifAnyGranted roles="ROLE_ADMIN">
    <h1>Create user-defined queries</h1><br/>
    <div>
    <h4>Test HQL code</h4>
    <p align="justify">To create a new workflow, you first need to define a query in the Hibernate Query Language (HQL) which extends SQL. In order to run such a query smoothly, you are able to test it by typing the HQL code into this text field.</p><br/>
    <g:form url="[action: 'execHQL']">

        <g:textArea id="hqlQuery" name="HQLQuery" style="width:100%; height: 50px"></g:textArea>
	<br/><br/>
        <g:submitButton name="submit" value="Test code"></g:submitButton>

    </g:form>
    </div>
    <br/>
    <h4>Queries on the CancerSysDB</h4>
    <div >
        <p align="justify">For general information about the HQL syntax, please refer to the official <a href="https://docs.jboss.org/hibernate/orm/3.3/reference/en/html/queryhql.html" target="_blank">online documentation</a>.</p><br/>
        <h6>General data model</h6>
        <p align="justify">This is the general data model of the CancerSysDB. There are four basic types of data:</p>
        <ul>
            <li class="structuraldata">Structural data: all data that manages the patients and samples</li>
            <li class="geneticdata"><a href="#DataTables" >Molecular data</a>: data that is derived from cancer genome analysis</li>
            <li class="clinicaldata"><a href="#ClinicValues" >Clinical data</a>: data associated to clinical course of a patient's disease</li>
            <li class="genedata">Genomic annotation: information on genes and meta data about these genes</li>
        </ul>
	<p align="justify">The relationships between these data types are illustrated in the following figure.</p><br/>



        <asset:image src="tutorial/DataModell4.svg" width="100%" />
<br/><br/><br/>
    <h6>Examples for JOIN operations on the database</h6>
        <p align="justify">Here is an examples which shows how the database can be queried. If you use it, please costumize <i>Data</i> with the data you want to access from the molecular data. The documentation on <a href="#DataTables" >Molecular data types</a> helps you to find the right data type and the fields provided by the data types.</p>
        <asset:image src="tutorial/QueryGraphExample2.svg" width="100%" /><br/><br/>



        <p align="justify">Here is a generic example for an HQL JOIN so you can use connect the data you need for your workflow.</p> <br/>


        <p style="background-color:#E6E6E6;padding:10px">
            SELECT ... <p style="background-color:#fffe6b;">FROM Patient p join p.study stud</p><p style="background-color:#bcaaff;"> join p.importinfo ii join ii.data cd </p><p style="background-color:#fffe6b;"> join p.samples s  join s.datasets ds</p><p style="background-color:#c0dab3;"> join ds.dataPeak d </p><p style="background-color:#7c996e;">join d.gene g </p>WHERE ....
	</p>

    The <a href="#DataTables" >molecular data types</a> help you finding the right data type and the fields provided by the data types.</p>

	<br/>	<br/>

        <a name="DataTables"><h4>Molecular data types</h4></a>
<p align="justify">This section lists all molecular data types with their fields in the CancerSysDB. The names of the tables together with field names should be pretty much self-explanatory.</p><br/>

    <g:render template="/geneticDataDescription" model="description:description"></g:render>


    </div>
</sec:ifAnyGranted>

</body>
</html>
