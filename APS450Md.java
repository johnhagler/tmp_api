/*
***************************************************************
*                                                             *
*                           NOTICE                            *
*                                                             *
*   THIS SOFTWARE IS THE PROPERTY OF AND CONTAINS             *
*   CONFIDENTIAL INFORMATION OF INFOR AND/OR ITS AFFILIATES   *
*   OR SUBSIDIARIES AND SHALL NOT BE DISCLOSED WITHOUT PRIOR  *
*   WRITTEN PERMISSION. LICENSED CUSTOMERS MAY COPY AND       *
*   ADAPT THIS SOFTWARE FOR THEIR OWN USE IN ACCORDANCE WITH  *
*   THE TERMS OF THEIR SOFTWARE LICENSE AGREEMENT.            *
*   ALL OTHER RIGHTS RESERVED.                                *
*                                                             *
*   (c) COPYRIGHT 2013 INFOR.  ALL RIGHTS RESERVED.           *
*   THE WORD AND DESIGN MARKS SET FORTH HEREIN ARE            *
*   TRADEMARKS AND/OR REGISTERED TRADEMARKS OF INFOR          *
*   AND/OR ITS AFFILIATES AND SUBSIDIARIES. ALL RIGHTS        *
*   RESERVED.  ALL OTHER TRADEMARKS LISTED HEREIN ARE         *
*   THE PROPERTY OF THEIR RESPECTIVE OWNERS.                  *
*                                                             *
***************************************************************
*/
package mvx.app.pgm;

import mvx.app.common.*;
import mvx.runtime.*;
import mvx.db.dta.*;
import mvx.app.util.*;
import mvx.app.plist.*;
import mvx.app.ds.*;
import mvx.util.*;

/*
*Modification area - M3
*Nbr            Date   User id     Description
*99999999999999 999999 XXXXXXXXXX  x
*Modification area - Business partner
*Nbr            Date   User id     Description
*99999999999999 999999 XXXXXXXXXX  x
*Modification area - Customer
*Nbr            Date   User id     Description
*99999999999999 999999 XXXXXXXXXX  x
*/


/**
*<BR><B><FONT SIZE=+2>Fnc: APS450 Metadata</FONT></B><BR><BR>
*
* This class ...<BR><BR>
*
*/
public class APS450Md extends Batch
{
   public void movexMain() {
      INIT();

      IN60 = false;
      this.MSGID.clear();
      this.MSGDTA.clear();
      if (XXOPC.EQ("GetBuild")) {
         // Only return current _build
      } else if (XXOPC.EQ("CrtMetaDta")) {
         createMetaData();
      } else {
         RERR();
      }
      // Always return current _build
      CRCRTPDRMdDS.setPABILD().moveLeftPad(_build);
      //   Return error
      APIDS.setPXIN60(toChar(IN60));
      if (!this.MSGID.isBlank()) {
         APIDS.setPXMSID().moveLeft(this.MSGID);
         APIDS.setPXMSGD().moveLeft(this.MSGDTA);
      } else {
         APIDS.setPXMSID().clear();
         APIDS.setPXMSGD().clear();
      }
      SETLR();
      return;
   }

  /**
   * creates program metadata for view program.
   *
   */
   public void createMetaData() {

      // Populate Sorting order program metadata in table CSYVIG
      CRCRTPMD.createSortingOrderProgramMetaData(APIDS.getPXCONO(), CRCRTPDRMdDS.getPAOPTA(), sortingOrderProgram);

      // Populate Sorting order table metadata in table CSYVIF
      CRCRTPMD.createSortingOrderTableMetaData(APIDS.getPXCONO(), CRCRTPDRMdDS.getPAOPTA(), sortingOrderTable);
      
      // Populate Valid Table fields
      CRCRTPMD.createFieldGroupFields(APIDS.getPXCONO(), CRCRTPDRMdDS.getPAOPTA(), viewTableFields);

      // Populate Non Table fields
      CRCRTPMD.createFieldGroupVirtualFields(APIDS.getPXCONO(), CRCRTPDRMdDS.getPAOPTA(), viewVirtualFields);

      // Populate Standard sorting order in table CSYVIU
      CRCRTPMD.createStandardSortingOrder(APIDS.getPXCONO(), CRCRTPDRMdDS.getPAOPTA(), sortingOrderStandard);

      // Populate View metadata in table CSYSPF and Inquiry type metadata in table CSYSPI
      CRCRTPMD.createViewMetaData(APIDS.getPXCONO(), CRCRTPDRMdDS.getPAOPTA(), viewProgram);

      // Populate Standard Views
      CRCRTPMD.createStandardViews(APIDS.getPXCONO(), CRCRTPDRMdDS.getPAOPTA(), viewStandard);
      
      // Populate Program Start Values
      CRCRTPMD.createProgramStartValues(APIDS.getPXCONO(), CRCRTPDRMdDS.getPAOPTA(), programStartValues);
   }

  /**
   *    RERR - Error routine
   */
   public void RERR() {
      IN60 = true;
      this.MSGDTA.moveLeftPad(XXOPC);
      //   MSGID=XOPC001 Invalid operation code &1
      this.MSGID.move("XOPC001");
   }

  /**
   *    SETLR - End of program
   */
   public void SETLR() {
      super.SETLR(false);
   }

  /**
   *    INIT - Init subroutine
   */
   public void INIT() {
   }

   // Movex MDB definitions

   // Movex MDB definitions end

   public void initMDB() {
   }

   public cCRCRTPMD CRCRTPMD = new cCRCRTPMD(this);
   public sAPIDS APIDS = new sAPIDS(this);
   public MvxString XXOPC = new MvxString(10);//*LIKE XXA10

   //*ENTRY PARAMS{

   public void unpackEntryParams(Object o) {//extract entry param
      MvxRecord mr = (MvxRecord)o;
      mr.reset();
      mr.getString(APIDS.setAPIDS());
      XXOPC.move(APIDS.getPXOPC());
      mr.getString(CRCRTPDRMdDS.setCRCRTPDRMdDS());
   }

   public void returnEntryParams(Object o) {//return entry param
      MvxRecord mr = (MvxRecord)o;
      mr.reset();
      mr.set(APIDS.getAPIDS());
      mr.set(CRCRTPDRMdDS.getCRCRTPDRMdDS());
   }

   //Datastructures for CRCRTPDR
   public sCRCRTPDRMdDS CRCRTPDRMdDS = new sCRCRTPDRMdDS(this);

   // Table with program meta data for Sorting order
   public final static String[][] sortingOrderProgram = {
      //  0 STUS = Selection table used 0/1           - 1,0 dec
      //  1 SOUS = Sorting option used 0/1/2          - 1,0 dec
      //  2 DSCE = Descending sorting possible 0/1    - 1,0 dec
      //  3 FILE = File                               - 10 char
      //  4 SNDU = Single division used 0/1           - 1,0 dec
      //  5 APSQ = Allowed panel sequence             - 30 char
     //STUS SOUS DSCE FILE          SNDU APSQ
      {"0", "2", "0", "FAPIBH    ", "1", "EFG                           "}
   };

   // Table with meta data for Sorting order table
   public final static String[][] sortingOrderTable = {
         //  0 FILE = File                               - 10 char
         //  2 MKE1 = Mandatory key 1                    - 6 char
        //  3 MKE2 = Mandatory key 2                    - 6 char
         //  4 MKE3 = Mandatory key 3                    - 6 char
         //  5 MKE4 = Mandatory key 4                    - 6 char

        //FILE          MKE1      MKE2      MKE3      MKE4
         {"FAPIBH    ", "      ", "      ", "      ", "      "} 
   }; 

   // 2D Array with meta data for Standard sorting order
   public final static String[][] sortingOrderStandard = {
      //  0 QTTP = Sorting order                      - 2,0 dec
      //  1 MSI1 = Message ID for Description         - 7 char
      //  2 MSI2 = Message ID for Name                - 7 char
      //  3 MSGF = Message file (if blank MVXCON used)- 10 char
      //  4 FILE = File                               - 10 char
      //  5 SOPT = Sorting option                     - 2 char
      //  6 NFTR = Number of filters (CONO not incl)  - 1,0 dec
      //  7 PAV1 = View 1                             - 10 char
      //  8 PAV2 = View 2                             - 10 char
      //  9 PAV3 = View 3                             - 10 char
      // 10 PAV4 = View 4                             - 10 char
      // 11 PAV5 = View 5                             - 10 char
      // 12 PAV6 = View 6                             - 10 char
      // 13 SNDI = Single division                    - 1,0 dec
      // 14 JNSO = Join sort                          - 1,0 dec
      // 15 MXRE = Max number of records              - 6,0 dec
      // 16 SLF1 = Selection field 1                  - 6 char
      // 17 SLF2 = Selection field 2                  - 6 char
      // 18 SLF3 = Selection field 3                  - 6 char
      // 19 AGGR = Aggregation                        - 1,0 dec
      // 20 AGRG = Aggregation drill down             - 1,0 dec
      // 21 SUB1 = Subtotal 1                         - 2,0 dec
      // 22 SUB2 = Subtotal 2                         - 2,0 dec
      // 23 SUB3 = Subtotal 3                         - 2,0 dec

     //QTTP  MSI1       MSI2       MSGF          FILE          SOPT  NFTR PAV1          PAV2          PAV3          PAV4          PAV5          PAV6          SNDI JNSO MXRE      SLF1      SLF2      SLF3      AGGR AGRG SUB1  SUB2  SUB3
      {"01", "       ", "       ", "          ", "FAPIBH    ", "00", "1", "STD01-01  ", "STD01-02  ", "          ", "          ", "          ", "          ", "1", "0", "000000", "E5IBTP", "E5SUPA", "      ", "0", "0", "00", "00", "00"},
      {"02", "       ", "       ", "          ", "FAPIBH    ", "10", "1", "STD02-01  ", "STD02-02  ", "          ", "          ", "          ", "          ", "1", "0", "000000", "E5IBTP", "E5SUPA", "      ", "0", "0", "00", "00", "00"},
      {"03", "       ", "       ", "          ", "FAPIBH    ", "20", "1", "STD03-01  ", "STD03-02  ", "          ", "          ", "          ", "          ", "1", "0", "000000", "E5IBTP", "E5SUPA", "      ", "0", "0", "00", "00", "00"},
      {"04", "       ", "       ", "          ", "FAPIBH    ", "30", "1", "STD04-01  ", "STD04-02  ", "          ", "          ", "          ", "          ", "1", "0", "000000", "E5IBTP", "E5SUPA", "      ", "0", "0", "00", "00", "00"},
      {"05", "       ", "       ", "          ", "FAPIBH    ", "40", "1", "STD05-01  ", "STD05-02  ", "          ", "          ", "          ", "          ", "1", "0", "000000", "E5IBTP", "E5SUPA", "      ", "0", "0", "00", "00", "00"}
   };

   // 2D Array with field group fields
   public final static String[][] viewTableFields = {

      //  0 OBJC = field name                       - 6 char
      //  1 FFIL = File name                        - 6 char
      //  2 POBJ = Popular field (0/1)              - 1,0 dec
      //  3 FHID = Field help ID                    - 8 char  (if blank, 4 last char from OBJC is used as field help)
      //  4 ECDE = Edit code  (in first position)   - 2 char
      //  5 FDCA = Field information code           - 3 char
      //  6 DCNY = Debit / credit code used (0/1)   - 1,0 dec  (Only valid in combination with Field information code A01)
      //  7 EDFL = Edit field (0/1)                 - 1 char   ("1" = the field should be open for input)

      //OBJC    FFIL      POBJ FHID        ECDE  FDCA   DCNY EDFL
      {"E5DIVI","FAPIBH", "1", "        ", "  ", "   ", "0", "1"}, // Division
      {"E5INBN","FAPIBH", "1", "        ", "  ", "   ", "0", "1"}, // Invoice batch number
      {"E5SPYN","FAPIBH", "1", "        ", "  ", "   ", "0", "1"}, // Payee
      {"E5SUNO","FAPIBH", "1", "        ", "  ", "   ", "0", "1"}, // Supplier number
      {"E5SINO","FAPIBH", "1", "        ", "  ", "   ", "0", "1"}, // Supplier invoice number
      {"E5IBTP","FAPIBH", "1", "        ", "  ", "   ", "0", "1"}, // Invoice batch type
      {"E5SUPA","FAPIBH", "1", "        ", "  ", "   ", "0", "0"}, // Invoice status
      {"E5MSID","FAPIBH", "1", "        ", "  ", "   ", "0", "0"}, // Message ID
      {"E5IMCD","FAPIBH", "1", "        ", "  ", "   ", "0", "1"}, // Invoice matching
      {"E5EALS","FAPIBH", "1", "        ", "  ", "   ", "0", "1"}, // EAN location code supplier
      {"E5VRNO","FAPIBH", "1", "        ", "  ", "   ", "0", "1"}, // VAT registration number
      {"E5EALP","FAPIBH", "1", "        ", "  ", "   ", "0", "1"}, // EAN location code payee
      {"E5EALR","FAPIBH", "1", "        ", "  ", "   ", "0", "1"}, // EAN location code consignee
      {"E5IVDT","FAPIBH", "1", "        ", "  ", "D01", "0", "1"}, // Invoice date
      {"E5DUDT","FAPIBH", "1", "        ", "  ", "D01", "0", "1"}, // Due date
      {"E5DNCO","FAPIBH", "1", "        ", "  ", "   ", "0", "1"}, // Document code
      {"E5VTAM","FAPIBH", "1", "        ", "  ", "A01", "0", "1"}, // VAT
      {"E5CUCD","FAPIBH", "1", "        ", "  ", "   ", "0", "1"}, // Currency
      {"E5CUAM","FAPIBH", "1", "        ", "  ", "A01", "0", "1"}, // Foreign currency amount
      {"E5TLNA","FAPIBH", "1", "        ", "  ", "A01", "0", "1"}, // Total line amount - net
      {"E5TASD","FAPIBH", "1", "        ", "  ", "A01", "0", "1"}, // Discount base amount
      {"E5TTXA","FAPIBH", "1", "        ", "  ", "A01", "0", "0"}, // Total taxable amount
      {"E5TCHG","FAPIBH", "1", "        ", "  ", "A01", "0", "0"}, // Total charges
      {"E5TOPA","FAPIBH", "1", "        ", "  ", "A01", "0", "0"}, // Total due
      {"E5PRPA","FAPIBH", "1", "        ", "  ", "A01", "0", "1"}, // Prepaid amount
      {"E5TECD","FAPIBH", "1", "        ", "  ", "   ", "0", "1"}, // Cash discount term
      {"E5CDT1","FAPIBH", "1", "        ", "  ", "D01", "0", "1"}, // Cash discount date 1
      {"E5CDP1","FAPIBH", "1", "        ", "  ", "   ", "0", "1"}, // Cash discount percentage
      {"E5CDC1","FAPIBH", "1", "        ", "  ", "A01", "0", "1"}, // Cash discount amount 1
      {"E5CDT2","FAPIBH", "1", "        ", "  ", "D01", "0", "1"}, // Cash discount date 2
      {"E5CDP2","FAPIBH", "1", "        ", "  ", "   ", "0", "1"}, // Cash discount percentage 2
      {"E5CDC2","FAPIBH", "1", "        ", "  ", "A01", "0", "1"}, // Cash discount amount 2
      {"E5CDT3","FAPIBH", "1", "        ", "  ", "D01", "0", "1"}, // Cash discount date 3
      {"E5CDP3","FAPIBH", "1", "        ", "  ", "   ", "0", "1"}, // Cash discount percentage 3
      {"E5CDC3","FAPIBH", "1", "        ", "  ", "A01", "0", "1"}, // Cash discount amount 3
      {"E5PYME","FAPIBH", "1", "        ", "  ", "   ", "0", "1"}, // Payment method - accounts payable
      {"E5TEPY","FAPIBH", "1", "        ", "  ", "   ", "0", "1"}, // Payment terms
      {"E5ARAT","FAPIBH", "1", "        ", "  ", "   ", "0", "1"}, // Exchange rate
      {"E5CRTP","FAPIBH", "1", "        ", "  ", "   ", "0", "1"}, // Exchange rate type
      {"E5PUDT","FAPIBH", "1", "        ", "  ", "D01", "0", "1"}, // Order date
      {"E5DEDA","FAPIBH", "1", "        ", "  ", "D01", "0", "1"}, // Delivery date
      {"E5FECN","FAPIBH", "1", "        ", "  ", "   ", "0", "1"}, // Future rate agreement number
      {"E5SERS","FAPIBH", "1", "        ", "  ", "   ", "0", "1"}, // Service code
      {"E5VSER","FAPIBH", "1", "        ", "  ", "   ", "0", "0"}, // Voucher number series
      {"E5VONO","FAPIBH", "1", "        ", "  ", "   ", "0", "0"}, // Voucher number
      {"E5ACDT","FAPIBH", "1", "        ", "  ", "D01", "0", "0"}, // Accounting date
      {"E5BIST","FAPIBH", "1", "        ", "  ", "   ", "0", "0"}, // Invoice progress
      {"E5IBHE","FAPIBH", "1", "        ", "  ", "   ", "0", "0"}, // Status batch head error
      {"E5IBLE","FAPIBH", "1", "        ", "  ", "   ", "0", "0"}, // Status batch line error
      {"E5UPBI","FAPIBH", "1", "        ", "  ", "   ", "0", "0"}, // Invoice per receiving number
      {"E5SUAC","FAPIBH", "1", "        ", "  ", "   ", "0", "0"}, // Supplier acceptance
      {"E5SBAD","FAPIBH", "1", "        ", "  ", "   ", "0", "0"}, // Conditions for adding lines
      {"E5PYAD","FAPIBH", "1", "        ", "  ", "   ", "0", "0"}, // Our invoicing address
      {"E5APCD","FAPIBH", "1", "        ", "  ", "   ", "0", "1"}, // Authorized user
      {"E5SDAP","FAPIBH", "1", "        ", "  ", "   ", "0", "1"}, // AP standard document
      {"E5DNRE","FAPIBH", "1", "        ", "  ", "   ", "0", "0"}, // Debit note reason
      {"E5SDA1","FAPIBH", "1", "        ", "  ", "   ", "0", "1"}, // Text line 1
      {"E5SDA2","FAPIBH", "1", "        ", "  ", "   ", "0", "1"}, // Text line 2
      {"E5SDA3","FAPIBH", "1", "        ", "  ", "   ", "0", "1"}, // Text line 3
      {"E5BSCD","FAPIBH", "1", "        ", "  ", "   ", "0", "1"}, // Base country
      {"E5FTCO","FAPIBH", "1", "        ", "  ", "   ", "0", "1"}, // From/to country
      {"E5ECAR","FAPIBH", "1", "        ", "  ", "   ", "0", "1"}, // State
      {"E5TDCD","FAPIBH", "1", "        ", "  ", "   ", "0", "1"}, // Trade code
      {"E5TXAP","FAPIBH", "1", "        ", "  ", "   ", "0", "1"}, // Tax applicable
      {"E5VDME","FAPIBH", "1", "        ", "  ", "   ", "0", "1"}, // VAT declaration method
      {"E5PUNO","FAPIBH", "1", "        ", "  ", "   ", "0", "1"}, // Purchase order number
      {"E5APCT","FAPIBH", "0", "        ", "  ", "   ", "0", "0"}, // Description
      {"E5REJD","FAPIBH", "0", "        ", "  ", "   ", "0", "0"}, // Rejection date
      {"E5RPAA","FAPIBH", "0", "        ", "  ", "   ", "0", "0"}, // Reprint after adjustment
      {"E5AAPD","FAPIBH", "0", "        ", "  ", "   ", "0", "0"}, // Approval date
      {"E5ADAB","FAPIBH", "0", "        ", "  ", "   ", "0", "0"}, // Adjusted amount
      {"E5CRNO","FAPIBH", "0", "        ", "  ", "   ", "0", "0"}, // Credit number
      {"E5YRE1","FAPIBH", "0", "        ", "  ", "   ", "0", "0"}, // Your reference
      {"E5SCRE","FAPIBH", "0", "        ", "  ", "   ", "0", "0"}, // Rejection reason
      {"E5TXID","FAPIBH", "0", "        ", "  ", "   ", "0", "0"}, // Text identity
      {"E5LMTS","FAPIBH", "0", "        ", "  ", "   ", "0", "0"}, // Timestamp
      {"E5RGDT","FAPIBH", "0", "        ", "  ", "D01", "0", "0"}, // Entry date
      {"E5RGTM","FAPIBH", "0", "        ", "  ", "D02", "0", "0"}, // Entry time
      {"E5LMDT","FAPIBH", "0", "        ", "  ", "D01", "0", "0"}, // Change date
      {"E5CHNO","FAPIBH", "0", "        ", "  ", "   ", "0", "0"}, // Change number
      {"E5CHID","FAPIBH", "0", "        ", "  ", "   ", "0", "0"}, // Changed by
      {"E5DNOI","FAPIBH", "0", "        ", "  ", "   ", "0", "0"}, // Original invoice number
      {"E5OYEA","FAPIBH", "0", "        ", "  ", "   ", "0", "0"}, // Original year
      {"E5PPYR","FAPIBH", "0", "        ", "  ", "   ", "0", "0"}, // Reference number
      {"E5PPYN","FAPIBH", "0", "        ", "  ", "   ", "0", "0"}, // Payment request  number
      {"E5YEA4","FAPIBH", "0", "        ", "  ", "   ", "0", "0"}, // Year
      {"E5BKID","FAPIBH", "0", "        ", "  ", "   ", "0", "0"}, // Bank account identity
      {"E5CORI","FAPIBH", "0", "        ", "  ", "   ", "0", "0"}, // Correlation ID
      {"IDSUNM","CIDMAS", "0", "        ", "  ", "   ", "0", "0"}, // Supplier name
      {"IDSUTY","CIDMAS", "0", "        ", "  ", "   ", "0", "0"}, // Supplier type
      {"IDALSU","CIDMAS", "0", "        ", "  ", "   ", "0", "0"}, // Search key
      {"IDSTAT","CIDMAS", "0", "        ", "  ", "   ", "0", "0"}, // Status
      {"IDCORG","CIDMAS", "0", "        ", "  ", "   ", "0", "0"}, // Organization number 1
      {"IDCOR2","CIDMAS", "0", "        ", "  ", "   ", "0", "0"}, // Organization number 2
      {"IDLNCD","CIDMAS", "0", "        ", "  ", "   ", "0", "0"}, // Language
      {"IDPHNO","CIDMAS", "0", "        ", "  ", "   ", "0", "0"}, // Telephone number 1
      {"IDCSCD","CIDMAS", "0", "        ", "  ", "   ", "0", "0"}, // Country
      {"IDECAR","CIDMAS", "0", "        ", "  ", "   ", "0", "0"}, // State
      {"IISUCL","CIDVEN", "0", "        ", "  ", "   ", "0", "0"}, // Supplier group
      {"IIBUYE","CIDVEN", "0", "        ", "  ", "   ", "0", "0"}, // Buyer
      {"IIRESP","CIDVEN", "0", "        ", "  ", "   ", "0", "0"}, // Responsible
      {"IIOUCN","CIDVEN", "0", "        ", "  ", "   ", "0", "0"}, // Our customer number at supplier
      {"IIPRSU","CIDVEN", "0", "        ", "  ", "   ", "0", "0"}, // Payee
      {"IICUCD","CIDVEN", "0", "        ", "  ", "   ", "0", "0"}, // Currency
      {"IICRTP","CIDVEN", "0", "        ", "  ", "   ", "0", "0"}, // Exchange rate type
      {"IIVTCD","CIDVEN", "0", "        ", "  ", "   ", "0", "0"} // VAT code
   };


   // 2D Array with field group &-fields
   public final static String[][] viewVirtualFields = {

      //  0 OBJC = field name                       - 6 char
      //  1 POBJ = Popular field (0/1)              - 1,0 dec
      //  2 FHID = Field help ID                    - 8 char  (if blank, 4 last char from OBJC is used as field help)
      //  3 OPMS = Message ID for                   - 7 char
      //  4 FLDT = Field type                       - 1 char
      //  5 FLDD = Number of digits                 - 3,0 dec
      //  6 FLDP = Number of decimal places (0 - 9) - 2,0 dec
      //  7 ECDE = Edit code  (in first position)   - 2 char
      //  8 FDCA = Field information code           - 3 char
      //  9 DCNY = Debit / credit code used (0/1)   - 1,0 dec  (Only valid in combination with Field information code A01)
      // 10 EDFL = Edit field (0/1)                 - 1 char   ("1" = the field should be open for input)
      // 11 VARI = Browse variant (00-99)           - 2 char  (Should correspont to array browseFields or browseCSYTABFields in program CRCRTPMD)

      //OBJC     POBJ FHID        OPMS       FLDT FLDD   FLDP  ECDE  FDCA   DCNY EDFL VARI
      {"      ", "0", "        ", "       ", " ", "   ", "00", "  ", "   ", "0", "0", "00"} 	
   };

   // 2D Array with meta data for view programs
   public final static String[][] viewProgram = {
      //  0 REAC = Responsible active = 3             - 1,0 dec
      //  1 MPVL = Max panel version length           - 3,0 dec
      //  2 SRTU = Sorting          0/1               - 1,0 dec
      //  3 MAXU = Max value        0/1               - 1,0 dec
      //  4 SUMA = Sum              0/1               - 1,0 dec
      //  5 OVRR = Override         0/1               - 1,0 dec

     //REAC MPVL   SRTU MAXU SUMA OVRR
      {"3", "270", "0", "0", "0", "0"}
   };

   // 2D Array with fields for standard views
   public final static String[][] viewStandard = {

      //  0 PAVR = View Name                        - 10 char
      //  1 MSI1 = Program heading for Description  -  7 char
      //  2 MSI2 = Program heading for Name         -  7 char
      //  3 MSGF = Message file (if blank MVXCON used)- 10 char
      //  4 QTTP = Sorting order                    - 2,0 dec
      //  5 FL01 = Field  1                         - 10 char
      //  6 FL02 = Field  2                         - 10 char
      //  7 FL03 = Field  3                         - 10 char
      //  8 FL04 = Field  4                         - 10 char
      //  9 FL05 = Field  5                         - 10 char
      // 10 FL06 = Field  6                         - 10 char
      // 11 FL07 = Field  7                         - 10 char
      // 12 FL08 = Field  8                         - 10 char
      // 13 FL09 = Field  9                         - 10 char
      // 14 FL10 = Field  10                        - 10 char
      // 15 FL11 = Field  11                        - 10 char
      // 16 FL12 = Field  12                        - 10 char
      // 17 FL13 = Field  13                        - 10 char
      // 18 FL14 = Field  14                        - 10 char
      // 19 FL15 = Field  15                        - 10 char
      // 20 FL16 = Field  16                        - 10 char
      // 21 FL17 = Field  17                        - 10 char
      // 22 FL18 = Field  18                        - 10 char
      // 23 FL19 = Field  19                        - 10 char
      // 24 FL20 = Field  20                        - 10 char
      // 25 FL21 = Field  21                        - 10 char
      // 26 FL22 = Field  22                        - 10 char
      // 27 FL23 = Field  23                        - 10 char
      // 28 FL24 = Field  24                        - 10 char
      // 29 FL25 = Field  25                        - 10 char
      // 30 FL26 = Field  26                        - 10 char
      // 31 FL27 = Field  27                        - 10 char
      // 32 FL28 = Field  28                        - 10 char
      // 33 FL29 = Field  29                        - 10 char
      // 34 FL30 = Field  30                        - 10 char

      //PAVR         MSI1       MSI2       MSGF          QTTP  FL01          FL02          FL03          FL04          FL05          FL06          FL07          FL08          FL09          FL10          FL11          FL12          FL13          FL14          FL15          FL16          FL17          FL18          FL19          FL20          FL21          FL22          FL23          FL24          FL25          FL26          FL27          FL28          FL29          FL30
      {"STD01-01  ", "       ", "       ", "          ", "01", "E5INBN    ", "E5SINO    ", "E5SPYN    ", "IDSUNM    ", "E5CUAM    ", "E5CUCD    ", "E5ACDT    ", "E5SUPA    ", "E5BIST    ", "E5IBHE    ", "E5IBLE    ", "E5IBTP    ", "E5IVDT  ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          "},
      {"STD01-02  ", "       ", "       ", "          ", "01", "E5INBN    ", "E5SINO    ", "E5SPYN    ", "E5CUAM    ", "E5CUCD    ", "E5IVDT    ", "E5SUPA    ", "E5BIST    ", "E5IVDT    ", "E5SCRE    ", "E5REJD    ", "E5RPAA    ", "E5AAPD  ", "E5ADAB    ", "E5CRNO    ", "E5YRE1    ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          "},
      {"STD02-01  ", "       ", "       ", "          ", "02", "E5SINO    ", "E5INBN    ", "E5SPYN    ", "IDSUNM    ", "E5CUAM    ", "E5CUCD    ", "E5ACDT    ", "E5IBTP    ", "E5BIST    ", "E5SUPA    ", "E5IVDT    ", "          ", "        ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          "},
      {"STD02-02  ", "       ", "       ", "          ", "02", "E5SINO    ", "E5INBN    ", "E5SPYN    ", "E5CUAM    ", "E5CUCD    ", "E5IVDT    ", "E5SUPA    ", "E5BIST    ", "E5IVDT    ", "E5SCRE    ", "E5REJD    ", "E5RPAA    ", "E5AAPD  ", "E5ADAB    ", "E5CRNO    ", "E5YRE1    ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          "},
      {"STD03-01  ", "       ", "       ", "          ", "03", "E5SUNO    ", "E5CUCD    ", "E5SINO    ", "E5SPYN    ", "IDSUNM    ", "E5CUAM    ", "E5CUCD    ", "E5ACDT    ", "E5SUPA    ", "E5BIST    ", "E5IBHE    ", "E5IBLE    ", "E5IBTP    ", "E5IVDT  ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          "},
      {"STD03-02  ", "       ", "       ", "          ", "03", "E5SUNO    ", "E5CUCD    ", "E5SINO    ", "E5CUAM    ", "E5CUCD    ", "E5IVDT    ", "E5SUPA    ", "E5BIST    ", "E5IVDT    ", "E5SCRE    ", "E5REJD    ", "E5RPAA    ", "E5AAPD  ", "E5ADAB    ", "E5CRNO    ", "E5YRE1    ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          "},
      {"STD04-01  ", "       ", "       ", "          ", "04", "E5SPYN    ", "E5SUNO    ", "E5IBTP    ", "E5SUPA    ", "IDSUNM    ", "E5CUAM    ", "E5CUCD    ", "E5ACDT    ", "E5BIST    ", "E5IVDT    ", "          ", "        ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          "},
      {"STD04-02  ", "       ", "       ", "          ", "05", "E5SPYN    ", "E5SUNO    ", "E5IBTP    ", "E5SUPA    ", "E5CUAM    ", "E5CUCD    ", "E5IVDT    ", "E5IVDT    ", "E5SCRE    ", "E5REJD    ", "E5RPAA    ", "E5AAPD  ", "E5ADAB    ", "E5CRNO    ", "E5YRE1    ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          "},
      {"STD05-01  ", "       ", "       ", "          ", "05", "E5SUNO    ", "E5SINO    ", "E5SPYN    ", "IDSUNM    ", "E5CUAM    ", "E5CUCD    ", "E5ACDT    ", "E5SUPA    ", "E5BIST    ", "E5IBHE    ", "E5IBLE    ", "E5IBTP    ", "E5IVDT  ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          "},
      {"STD05-02  ", "       ", "       ", "          ", "06", "E5SUNO    ", "E5SINO    ", "E5SPYN    ", "E5CUAM    ", "E5CUCD    ", "E5IVDT    ", "E5SUPA    ", "E5BIST    ", "E5IVDT    ", "E5SCRE    ", "E5REJD    ", "E5RPAA    ", "E5AAPD  ", "E5ADAB    ", "E5CRNO    ", "E5YRE1    ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          ", "          "}
   };
   
   // Table with standard Program start values
   public final static String[][] programStartValues = {
         //  0 PGN1 = To program                         - 10 char
         //  1 PGN2 = From program                       - 10 char
         //  2 OPT2 = Option                             - 2 char
         //  3 QTTP = Sorting order                      - 2,0 dec
         //  4 PAVR = View Name                          - 10 char
         //  5 PSEQ = Panel sequence                     - 10 char
         //  6 SPIC = Opening panel                      - 1 char

        //PGN1          PGN2          OPT2  QTTP  PAVR          PSEQ          SPIC
         {"APS450    ", "PPS118    ", "  ", "02", "          ", "          ", " "},             
   };

   public String getVarList(java.util.Vector v) {
      super.getVarList(v);
      v.addElement(CRCRTPMD);
      v.addElement(CRCRTPDRMdDS);
      v.addElement(APIDS);
      v.addElement(XXOPC);
      return version;
   }

   public void clearInstance() {
      super.clearInstance();
   }

   public String getVersion() {
      return _version;
   } //·end of method getVersion 	

   public String getRelease() {
      return _release;
   } //·end of method getRelease 	

   public String getSpLevel() {
      return _spLevel;
   } //·end of method getSpLevel 	

   public String getSpNumber() {
      return _spNumber;
   } //·end of method getSpNumber 	

   public final static String _version = "15";
   public final static String _release = "1";
   public final static String _spLevel = "2";
   public final static String _spNumber = "*NONE";
   public final static String _GUID = "518ED67EE11D4d7792037AEC969FEEE9";
   public final static String _tempFixComment = "";
   public final static String _build = "000000000000107";
   public final static String _pgmName = "APS450Md";

   public String getGUID() {
      return _GUID;
   } //·end of method getGUID

   public String getTempFixComment() {
      return _tempFixComment;
   } //·end of method getTempFixComment

   public String getVersionInformation() {
      return _version + '.' + _release + '.' + _spLevel + ':' + _spNumber;
   } //·end of method getVersionInformation

   public String getBuild() {
      return (_version + _release + _build + "      " + _pgmName + "                                   ").substring(0, 34);
   } //·end of method getBuild

   public String[][] getStandardModification() {
      return _standardModifications;
   } //·end of method [][] getStandardModification

  public final static String [][] _standardModifications={};
}
